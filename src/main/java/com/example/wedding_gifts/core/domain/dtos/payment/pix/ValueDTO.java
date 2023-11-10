package com.example.wedding_gifts.core.domain.dtos.payment.pix;

public record ValueDTO(
    String original
){

    public String toString(){
        String out = "\"valor\": {\"original\": \"%s\"}";
        return String.format(
            out,
            this.original
        );
    }

}
