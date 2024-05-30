package com.gotravel.gotravel.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.BookingConverter;
import com.gotravel.gotravel.converter.CategoryConverter;
import com.gotravel.gotravel.dto.BookingDTO;
import com.gotravel.gotravel.dto.BookingTourDateDTO;
import com.gotravel.gotravel.dto.CategoryDTO;
import com.gotravel.gotravel.dto.TourDTO;
import com.gotravel.gotravel.entity.Booking;
import com.gotravel.gotravel.entity.Category;
import com.gotravel.gotravel.entity.Tour;
import com.gotravel.gotravel.entity.TourCategory;
import com.gotravel.gotravel.enums.ConfirmationBooking;
import com.gotravel.gotravel.repository.BookingResponsitory;
import com.gotravel.gotravel.service.impl.IBookingService;
import com.gotravel.gotravel.service.impl.ITourService;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingService implements IBookingService {

	@Autowired
	private BookingResponsitory bookingResponsitory;

	@Autowired
	private BookingConverter bookingConverter;

	@Autowired
	private ITourService tourService;

	@Autowired
	private CategoryConverter categoryConverter;

	@Override
	public List<BookingDTO> findAll() {

		List<Booking> bookings = bookingResponsitory.findAll();

		List<BookingDTO> bookingDTOs = new ArrayList<>();

		for (Booking b : bookings) {
			BookingDTO bookingDTO = bookingConverter.toDTO(b);
			bookingDTOs.add(bookingDTO);
		}

		return bookingDTOs;
	}

	@Override
	public void removeAllBooking() {
		bookingResponsitory.deleteAll();
	}

	@Override
	public BookingDTO findById(UUID id) {
		Booking booking;
		booking = bookingResponsitory.findById(id).get();
		return bookingConverter.toDTO(booking);
	}

	@Override
	public BookingDTO save(BookingDTO bookingDTO) {

		Booking newBooking = new Booking();

		newBooking = bookingConverter.toEntity(bookingDTO);

		newBooking.setCheckIn(bookingDTO.getCheckInDate());
		newBooking.setCheckOut(bookingDTO.getCheckOutDate());
		newBooking.setNumGuest(bookingDTO.getNumGuest());
		newBooking.setTotalPrice(bookingDTO.getTotal());
		newBooking.setStatus(false);
		newBooking.setConfirmation(ConfirmationBooking.RESERVE);

		LocalDateTime today = LocalDateTime.now();
		newBooking.setCreateAt(today);

		bookingResponsitory.save(newBooking);

		return bookingConverter.toDTO(newBooking);
	}

	@Override
	public BookingDTO update(UUID bookingId, BookingDTO updateBookingDTO) {

		return null;
	}

	// update booking confirm
	@Override
	public void updateBookingConfirm(UUID bookingId, String confirm) {

		Optional<Booking> bookingOp = bookingResponsitory.findById(bookingId);

		if (!bookingOp.isPresent()) {
			throw new RuntimeException("Booking with ID " + bookingId + " not found.");
		}

		try {
			Booking booking = bookingOp.get();
			ConfirmationBooking confirmationBooking = Enum.valueOf(ConfirmationBooking.class, confirm);
			if (!booking.getConfirmation().equals(confirmationBooking)) {
				booking.setConfirmation(confirmationBooking);
				bookingResponsitory.save(booking);
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Giá trị trạng thái không hợp lệ: " + confirm);
		}

	}

	// update booking status
	@Override
	public void updateBookingStatus(UUID bookingId, boolean status) {
		Optional<Booking> bookingOptional = bookingResponsitory.findById(bookingId);

		if (!bookingOptional.isPresent()) {
			throw new RuntimeException("Booking with ID " + bookingId + "not found.");
		}

		Booking booking = bookingOptional.get();

		if (booking.getStatus() != status) {
			booking.setStatus(status);
			booking.setConfirmation(ConfirmationBooking.valueOf(ConfirmationBooking.CONFIRMED.name()));
			bookingResponsitory.save(booking);
		}

	}

	// return all booking of host
	@Override
	public List<BookingDTO> returnAllBookingOfHost(UUID userId, String confirmation) {

		// chuyển chuỗi filter thành hằng số enum của lớp ConfirmationBooking
		ConfirmationBooking confirmations = Enum.valueOf(ConfirmationBooking.class, confirmation);

//		// nếu như filter rỗng thì set confirmation mặc đinhj là PENDING
//		if (confirmation.isEmpty()) {
//			confirmations = ConfirmationBooking.PENDING;
//		}

		// Lấy ra danh sách tất cả các tour của user
		List<TourDTO> tours = tourService.getAllTourOfUser(userId);

		// lấy ra các tourId của các tours
		List<UUID> tourIds = tours.stream().map(TourDTO::getTourId).toList();

		// Lọc và trả về danh sách booking
		List<BookingDTO> bookings = new ArrayList<>();
		for (UUID tourId : tourIds) {
			// lấy ra tất cả các booking theo tourId và confirmation
			List<Booking> tourBookings = bookingResponsitory
					.findAllByTourTourIdAndConfirmationOrderByCreateAtDesc(tourId, confirmations);
			// convert booking sang bookingDTO
			List<BookingDTO> tourBookingDTOs = tourBookings.stream().map(bookingConverter::toDTO).toList();
			// add bookingDTO vào ds bookings
			bookings.addAll(tourBookingDTOs);
		}

		return bookings;
	}

	@Override
	public void remove(UUID id) {

		if (bookingResponsitory.existsById(id)) {
			bookingResponsitory.deleteById(id);
		} else {
			throw new EntityNotFoundException("Không tìm thấy thông tin đặt tour với id: " + id);
		}

	}

	@Override
	public List<BookingDTO> returnAllMyBookings(UUID userId) {

		List<Booking> bookings = bookingResponsitory.findAllByUserId(userId);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	@Override
	public List<UUID> updateBookingStatusWithSchedule() throws IOException {

		// lấy ra danh sách tất cả các booking
		List<Booking> bookings = bookingResponsitory.findAll();

		LocalDate currentDate = LocalDate.now();
		List<UUID> updateBooking = new ArrayList<>();

		// lặp qua từng booking để kiểm tra và cập nhật trạng thái
		for (Booking booking : bookings) {
			LocalDate checkInDate = booking.getCheckIn().toLocalDate(); // chuyển đổi date thành localDate
			LocalDate checkOutDate = booking.getCheckOut().toLocalDate();

			if (booking.getConfirmation() == ConfirmationBooking.CONFIRMED && checkInDate.equals(currentDate)
					&& currentDate.isBefore(checkOutDate)) {
				// nếu ngày hiện tại bằng với ngày checkIn và trước ngày checkout
				// lấy trạng thái CONFIRMED - sắp diễn ra
				// cập nhật trạng thái thành IN_PROGRESS
				booking.setConfirmation(ConfirmationBooking.IN_PROGRESS);
				updateBooking.add(booking.getBookingId());
			} else if (booking.getConfirmation() == ConfirmationBooking.IN_PROGRESS
					&& checkOutDate.equals(currentDate)) {
				// nếu ngày hiện tại sau ngày checkout
				// lấy trạng thái IN_PROGRESS
				// cập nhật trạng thái booking thành COMPLETED
				booking.setConfirmation(ConfirmationBooking.COMPLETED);
				updateBooking.add(booking.getBookingId());
			}
			bookingResponsitory.save(booking);

		}

		return updateBooking;
	}

	// lấy ra tất cả booking của tour
	@Override
	public List<BookingDTO> findAllBookingsForTourAndUser(UUID tourId, UUID userId) {

		List<Booking> bookings = bookingResponsitory.findAllByTourTourIdAndUserId(tourId, userId);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	// lấy tất cả booking của 1 tour có chung ngày bắt đầu
	@Override
	public List<BookingDTO> findAllBookingsByTourAndCheckIn(UUID tourId, Date checkIn) {

		List<Booking> bookings = bookingResponsitory.findAllByTourAndCheckIn(tourId, checkIn);

		List<BookingDTO> bookingDTOs = bookings.stream().map(bookingConverter::toDTO).toList();

		return bookingDTOs;
	}

	// trả về danh sách các tour được booking của user
	// nếu booking có chung 1 tour và chung ngày bắt đầu thì sẽ gộp tổng lại
	// tổng hợp tất cả các booking của 1 tour có chung ngày bắt đầu thành 1 đối
	// tượng booking mới

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public Page<BookingTourDateDTO> returnAllBookingOfUserWithTourIdAndCheckIn(UUID userId, Date checkInDateFilter,
			Date checkOutDateFilter, ConfirmationBooking confirmationFilter, String categoryName, String keyword,
			UUID categoryId, Pageable pageable) {

		// Chuyển đổi keyword về chữ thường
		String lowerCaseKeyword = keyword != null ? keyword.toLowerCase() : null;

		// Lấy ra danh sách tất cả các tour của user
		List<TourDTO> tours = tourService.getAllTourOfUser(userId);

		// lấy ra các tourId của các tours
		List<UUID> tourIds = tours.stream().map(TourDTO::getTourId).toList();

		// danh sách map để tổng hợp các booking theo ngày bắt đầu
		Map<String, BookingTourDateDTO> bookingSummaryMap = new HashMap<>();

		for (UUID tourId : tourIds) {
			// lấy ra tất cả các booking của tour đó
			List<Booking> bookings = bookingResponsitory.findAllByTour_Id(tourId).stream()
					// lọc ds loại bỏ những trạng thái REJECTED
					.filter(booking -> booking.getStatus() && booking.getConfirmation() != ConfirmationBooking.RESERVE
							&& booking.getConfirmation() != ConfirmationBooking.CANCEL)
					// lọc theo checkInDate nếu có
					.filter(booking -> checkInDateFilter == null || booking.getCheckIn().equals(checkInDateFilter))
					// lọc theo checkOutDate nếu có
					.filter(booking -> checkOutDateFilter == null || booking.getCheckOut().equals(checkOutDateFilter))
					// lọc theo confirmation nếu có
					.filter(booking -> confirmationFilter == null
							|| booking.getConfirmation().equals(confirmationFilter))

					// lọc theo categoryName nếu có
					.filter(booking -> categoryId == null || booking.getCategories().stream()
							.anyMatch(category -> category.getCategoryId().equals(categoryId)))

					// lọc theo từ khóa nếu có
					.filter(booking -> lowerCaseKeyword == null
							|| booking.getTour().getTourName().toLowerCase().contains(lowerCaseKeyword)
							|| booking.getTour().getProvince().toLowerCase().contains(lowerCaseKeyword))
					.collect(Collectors.toList());

			for (Booking b : bookings) {

				Date checkInDate = b.getCheckIn();
				String confirmation = b.getConfirmation().name();

				// tạo key kết hợp từ tourId, checkInDate và confirmation
				String key = tourId + "_" + checkInDate + "_" + confirmation;

				// Kiểm tra xem đã có BookingTourDateDTO nào tương ứng với key của booking này
				// chưa
				BookingTourDateDTO bookingTourDateDTO = bookingSummaryMap.get(key);

				if (bookingTourDateDTO == null) {
					// nếu chưa có, tạo mới
					bookingTourDateDTO = new BookingTourDateDTO();
					// thiết lập thông tin từ tour
					bookingTourDateDTO.setTourId(b.getTour().getTourId());
					bookingTourDateDTO.setTourName(b.getTour().getTourName());
					bookingTourDateDTO.setProvince(b.getTour().getProvince());
					bookingTourDateDTO.setDistrict(b.getTour().getDistrict());
					bookingTourDateDTO.setWard(b.getTour().getWard());
					bookingTourDateDTO.setNumGuest(b.getTour().getNumguest());
					bookingTourDateDTO.setTourTime(b.getTour().getTourTime());

					List<CategoryDTO> listCates = new ArrayList<>();
//					List<CategoryDTO> categoriesDTO = listCate.stream().map(categoryConverter::toDTO).toList();

					if (!b.getTour().getTourCategories().isEmpty()) {

						for (TourCategory cate : b.getTour().getTourCategories()) {
							listCates.add(categoryConverter.toDTO(cate.getCategory()));
						}
					}

					bookingTourDateDTO.setCategories(listCates);

					// thiết lập thông tin từ booking
					bookingTourDateDTO.setBookingId(b.getBookingId());
					bookingTourDateDTO.setCheckInDate(b.getCheckIn());
					bookingTourDateDTO.setCheckOutDate(b.getCheckOut());
					bookingTourDateDTO.setConfirmation(confirmation);

					// khởi tạo tổng tiền
					bookingTourDateDTO.setTotalPriceBooked(0.0);

					// lưu BookingTourDateDTO vào map với key là key vừa tạo
					bookingSummaryMap.put(key, bookingTourDateDTO);
				}

				// cập nhật số lượng khách và tổng tiền
				bookingTourDateDTO.setNumGuestBooked(bookingTourDateDTO.getNumGuestBooked() + b.getNumGuest());
				bookingTourDateDTO.setTotalPriceBooked(bookingTourDateDTO.getTotalPriceBooked() + b.getTotalPrice());
			}

		}

		// convert map value sang to list
		List<BookingTourDateDTO> bookingTourDateList = new ArrayList<>();
		
		// Sắp xếp danh sách theo thời gian check-in mới nhất
		bookingTourDateList = bookingSummaryMap.values().stream()
		    .sorted(Comparator.comparing(BookingTourDateDTO::getCheckInDate).reversed())
		    .collect(Collectors.toList());

		// Sử dụng các giá trị mới của page và pageSize từ tham số truy vấn
		int currentPage = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		// Tính toán lại vị trí bắt đầu và kết thúc của dữ liệu dựa trên các giá trị mới
		// này
		int start = currentPage * pageSize;
		int end = Math.min(start + pageSize, bookingTourDateList.size());

		// Kiểm tra và xử lý trường hợp đặc biệt
		if (bookingTourDateList.isEmpty() || start >= bookingTourDateList.size()) {
			// Trường hợp danh sách trống hoặc page lớn hơn số trang có sẵn
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		// Đảm bảo rằng các giá trị tính toán được nằm trong phạm vi hợp lệ của danh
		// sách dữ liệu
		start = Math.min(start, bookingTourDateList.size() - 1);
		end = Math.min(end, bookingTourDateList.size());

		// Trích xuất danh sách con từ danh sách dữ liệu với các giá trị mới của page và
		// pageSize
		List<BookingTourDateDTO> paginatedList = bookingTourDateList.subList(start, end);

		pageable = PageRequest.of(currentPage, 9);

		// Trả về kết quả của trang phân trang với dữ liệu được cập nhật
		return new PageImpl<>(paginatedList, pageable, bookingTourDateList.size());

	}

	// lấy ra tour theo trạng thái
	// đếm số nếu có 10 booking chung 1 tour và cùng 1 trạng thái thì trả về là 1
	// tour với trạng thái đó

	@Override
	public int countToursByTourIdAndConfirmation(UUID userId, String confirmation) {

		// Lấy ra tất cả tour của user
		List<TourDTO> toursUser = tourService.getAllTourOfUser(userId);

		// lấy ra tất cả id của toursUser
		List<UUID> tourIds = toursUser.stream().map(TourDTO::getTourId).toList();

		// số lượng booking có trạng thái tương ứng
		int count = 0;

		// lấy ra danh sách bookings
		for (UUID tourId : tourIds) {
			List<Booking> bookings = bookingResponsitory.findAllByTour_Id(tourId).stream()
					// lọc ds loại bỏ những trạng thái REJECTED
					.filter(booking -> booking.getStatus() && booking.getConfirmation() != ConfirmationBooking.RESERVE
							&& booking.getConfirmation() != ConfirmationBooking.CANCEL
							&& booking.getConfirmation() != ConfirmationBooking.PENDING)
					.toList();

			// sử dụng một set để lưu trữ các trạng thái kiểm tra của mỗi tour
			Set<String> checked = new HashSet<>();

			// lọc qua danh sách booking để lấy ra tour và trạng thái tương ứng
			for (Booking book : bookings) {
				// Nếu trạng thái booking trùng với trạng thái yêu cầu và chưa được đếm cho tour
				// này
				if (book.getConfirmation().name().equalsIgnoreCase(confirmation)
						&& !checked.contains(book.getConfirmation().name())) {
					count++;
					// Thêm trạng thái vào Set để không đếm lại lần nữa
					checked.add(book.getConfirmation().name());
				}
			}

		}

		// mục đích là đếm số lượng tour tương ứng với trạng thái của user
		return count;

	}

	// FILTER
	@Override
	public List<BookingDTO> filterBookings(Date checkInDate, Date checkOutDate, ConfirmationBooking confirmation) {

		Specification<Booking> spec = Specification.where(null);

		if (checkInDate != null) {
			spec = spec.and(BookingSpecification.hasCheckInDate(checkInDate));
		}

		if (checkOutDate != null) {
			spec = spec.and(BookingSpecification.hasCheckOutDate(checkOutDate));
		}
//        if (category != null) {
//        	UUID categoryId = UUID.fromString(category); // Chuyển đổi từ String thành UUID
//            spec = spec.and(BookingSpecification.hasCategory(categoryId));
//        }
		if (confirmation != null) {
			spec = spec.and(BookingSpecification.hasConfirmation(confirmation));
		}

		List<Booking> bookings = bookingResponsitory.findAll(spec);

		return bookings.stream().map(bookingConverter::toDTO).toList();
	}

}
