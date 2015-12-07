package com.github.niqdev.openwebnet.message;

/*  TODO
 *  frame = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, *, #}
 *  starts with '*'
 *  ends with '##'
 *  separator among the tags '*'
 *  *tag1*tag2*tag3*...*tagN##
 *
 *  tag = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, #}
 *  tag can't have the couple '##'
 */
public interface OpenMessage {

    String ACK = "*#*1##";
    String NACK = "*#*0##";
    String FRAME_START = "*";
    String FRAME_END = "##";

    String getValue();

}
