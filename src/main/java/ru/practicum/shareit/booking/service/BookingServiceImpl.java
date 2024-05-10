package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDto create(BookingShortDto bookingShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not possible create Booking - " +
                        "Not found User with Id " + userId));
        Item item = itemRepository.findById(bookingShortDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Not possible create Booking - " +
                        "Not found Item with Id " + bookingShortDto.getItemId()));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Not possible create booking - " +
                    "User cannot book a thing belonging to him");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Not possible create Booking - " +
                    "this item is not available");
        }
        Booking booking = toBooking(bookingShortDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            throw new BadRequestException("Not possible create Booking - " +
                    "the end date of the booking cannot be earlier than the start date of the booking");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);

        return toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Not possible create Booking - " +
                        "Not found Booking with Id " + bookingId));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Not possible create booking - " +
                    "Not found Booking with Id " + bookingId + " for user with an id" + userId);
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("It is not possible to confirm the Booking - " +
                    "the booking has already been confirmed or declined");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        return toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("There is no Booking with Id " + bookingId));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Only the Booking author can view the booking details " +
                    "or the owner of Item");
        }

        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size) {
        validationUserAndFrom(userId, from);

        try {
            List<Booking> bookings;
            Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByItemOwnerId(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(),
                            pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(),
                            pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, REJECTED, pageable);
                    break;
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
            return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size) {
        validationUserAndFrom(userId, from);

        try {

            List<Booking> bookings;
            Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerId(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, REJECTED, pageable);
                    break;
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
            return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public void validationUserAndFrom(Long userId, Integer from) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }
    }
}