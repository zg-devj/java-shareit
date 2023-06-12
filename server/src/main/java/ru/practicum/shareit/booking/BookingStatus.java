package ru.practicum.shareit.booking;

/*
Состояния:
WAITING - Ожидание подтверждения/отклонения
APPROVED - Бронирование одобрено владельцем
REJECTED - Отклонено владельцем
CANCELED - Отменено бронировавшем
 */
public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public String toString() {
        return this.name();
    }
}
