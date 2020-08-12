package com.devyk.aveditor.stream

/**
 * <pre>
 *     author  : devyk on 2020-07-16 21:29
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PacketType
 * </pre>
 */
public enum class PacketType {
    FIRST_AUDIO(1),
    FIRST_VIDEO(2),
    SPS_PPS(3),
    AUDIO(4),
    KEY_FRAME(5),
    VIDEO(6);


    var type = -1;
    constructor(types: Int){
        type = types
    }


}