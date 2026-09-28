
package com.bookmyroom.bookmyroom.room;

import com.bookmyroom.bookmyroom.booking.BookingRepository;

import com.bookmyroom.bookmyroom.exceptions.BookingConflictException;
import com.bookmyroom.bookmyroom.exceptions.DuplicateResourceException;
import com.bookmyroom.bookmyroom.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    // 1. Add a new room
    public Room addRoom(Room room) {

        if (roomRepository.existsByRoomNumber(
                room.getRoomNumber())) {

            throw new DuplicateResourceException(
                    "Room number already exists: "
                            + room.getRoomNumber()
            );
        }

        return roomRepository.save(room);
    }

    // 2. Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // 3. Get a room by ID
    public Room getRoomById(Long id) {

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: " + id
                        )
                );
    }

    // 4. Update an existing room
    public Room updateRoom(Long id, Room updatedRoom) {

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: " + id
                        )
                );

        // Check whether the room number is changing
        if (!existingRoom.getRoomNumber()
                .equals(updatedRoom.getRoomNumber())) {

            if (roomRepository.existsByRoomNumber(
                    updatedRoom.getRoomNumber())) {

                throw new DuplicateResourceException(
                        "Room number already exists: "
                                + updatedRoom.getRoomNumber()
                );
            }
        }

        existingRoom.setRoomNumber(
                updatedRoom.getRoomNumber()
        );

        existingRoom.setRoomType(
                updatedRoom.getRoomType()
        );

        existingRoom.setCapacity(
                updatedRoom.getCapacity()
        );

        existingRoom.setRatePerNight(
                updatedRoom.getRatePerNight()
        );

        return roomRepository.save(existingRoom);
    }

    // 5. Delete a room
    @Transactional
    public void deleteRoom(Long id) {

        Room room = roomRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found: " + id
                        )
                );

        // Preserve confirmed, cancelled and completed bookings
        if (bookingRepository.existsByRoomRoomId(id)) {

            throw new BookingConflictException(
                    "Cannot delete room "
                            + room.getRoomNumber()
                            + " because it has booking history"
            );
        }

        roomRepository.delete(room);
    }

    // 6. Count total rooms
    public long countRooms() {
        return roomRepository.count();
    }
}