package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JUnit 5 Nested")
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "Alex", "alex.b@yandex.ru");
        user2 = new User(2L, "Bill", "bill.d@yandex.ru");
        item = new Item(1L, "bag", "description", true, user1,
                null);
        booking = new Booking(1L, start, end, item, user1, BookingStatus.WAITING);
    }

    @Nested
    @DisplayName("Tests for the method getById")
    class GetById {
        @Test
        void getById_shouldReturnBookingDto() {
            Long bookingId = 1L;
            Long userId = 1L;
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getById(bookingId, userId);

            assertEquals(1L, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(BookingStatus.WAITING, result.getStatus());
            assertEquals(1L, result.getItem().getId());
            assertEquals("bag", result.getItem().getName());
            assertEquals(1L, result.getBooker().getId());
            assertEquals("Alex", result.getBooker().getName());
        }

        @Test
        void getById_shouldReturnUserNotFoundException() {
            Long bookingId = 1L;
            Long userId = 999L;

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(NotFoundException.class, () -> bookingService.getById(bookingId, userId));
        }
    }

    @Test
    void getById_shouldReturnBookingNotFoundException() {
        Long bookingId = 999L;
        Long userId = 1L;

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(bookingId, userId));
    }

    @Nested
    @DisplayName("Tests for the method getAllByOwnerId")
    class GetAllByOwnerId {
        @Test
        void getAllByOwnerId_whenStateIsAll() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.ALL);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerId(Mockito.anyLong(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.CURRENT);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(Mockito.anyLong(), Mockito.any(),
                    Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.PAST);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndBefore(Mockito.anyLong(), Mockito.any(),
                    Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.FUTURE);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartAfter(Mockito.anyLong(), Mockito.any(),
                    Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.WAITING);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.REJECTED);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(),
                    Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByOwnerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByOwnerId_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            assertThrows(ValidationException.class, () -> bookingService.getAllByOwnerId(userId, state, 0, 10));
        }

        @Test
        void getAllByOwnerId_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            String state = String.valueOf(BookingState.ALL);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

            assertThrows(NotFoundException.class, () -> bookingService.getAllByOwnerId(userId, state, 0, 10));
        }
    }

    @Nested
    @DisplayName("Tests for the method getAllByBookerId")
    class GetAllByBookerId {
        @Test
        void getAllByBookerId_whenStateIsAll() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.ALL);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsCURRENT() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.CURRENT);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(), Mockito.any(),
                    Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsPAST() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.PAST);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerIdAndEndBefore(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsFUTURE() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.FUTURE);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerIdAndStartAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsWAITING() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.WAITING);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBookerId_whenStateIsREJECTED() {
            Long userId = 1L;
            String state = String.valueOf(BookingState.REJECTED);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
            Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                    .thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllByBookerId(userId, state, 0, 10);

            assertEquals(1, result.size());
        }

        @Test
        void getAllByBooker_shouldThrowUnsupportedStatusException() {
            Long userId = 1L;
            String state = "BLABLABLA";

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

            assertThrows(ValidationException.class, () -> bookingService.getAllByBookerId(userId, state, 0, 10));
        }

        @Test
        void getAllByBookerId_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            String state = String.valueOf(BookingState.ALL);

            Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

            assertThrows(NotFoundException.class, () -> bookingService.getAllByBookerId(userId, state, 0, 10));
        }
    }

    @Nested
    @DisplayName("Tests for the method create")
    class Create {
        @Test
        void create_shouldSaveBooking() {
            BookingShortDto dto = new BookingShortDto(user2.getId(), start, end, item.getId());

            Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
            Mockito.when(bookingRepository.save(Mockito.any()))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

            BookingDto bookingdto = bookingService.create(dto, user2.getId());

            assertThat(bookingdto).hasFieldOrProperty("id");
        }

        @Test
        void create_shouldReturnUserNotFoundException() {
            Long userId = 999L;
            Long itemId = 1L;

            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

            BookingShortDto dto = new BookingShortDto(userId, start, end, itemId);

            assertThrows(NotFoundException.class, () -> bookingService.create(dto, userId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void create_shouldReturnItemNotFoundException() {
            Long userId = 2L;
            Long itemId = 999L;

            Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user2));

            BookingShortDto dto = new BookingShortDto(userId, start, end, itemId);

            assertThrows(NotFoundException.class, () -> bookingService.create(dto, userId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void create_shouldReturnValidationException() {
            Long userId = 2L;
            Long itemId = 1L;
            item.setAvailable(false);

            Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user2));

            BookingShortDto dto = new BookingShortDto(userId, start, end, itemId);

            assertThrows(BadRequestException.class, () -> bookingService.create(dto, userId));

            Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void approve() {
            BookingDto bookingDto = BookingMapper.toBookingDto(booking);
            BookingDto bookingDtoTest;

            Mockito.when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(booking));
            bookingDtoTest = bookingService.approve(1L,1L, true);
            bookingDto.setStatus(BookingStatus.APPROVED);

            assertThat(bookingDtoTest).isEqualTo(bookingDto);
        }
    }
}
