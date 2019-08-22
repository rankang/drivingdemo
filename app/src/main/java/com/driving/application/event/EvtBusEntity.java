package com.driving.application.event;

public class EvtBusEntity {

    public EvtBusEntity(int msgId, byte[] data) {
        this.msgId = msgId;
        this.data = data;
    }

    public int msgId;
    public byte[] data;
}
