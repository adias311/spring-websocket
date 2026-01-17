package com.synesthesia.websocket.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {

    private String roomId;

    private String type;

}
