package com.github.aesh

import com.aliucord.Utils
import com.aliucord.utils.RxUtils.subscribe
import com.discord.api.message.Message
import com.discord.models.domain.NonceGenerator
import com.discord.restapi.RestAPIParams
import com.discord.utilities.rest.RestAPI
import com.discord.utilities.time.ClockFactory

import rx.Subscriber

class sendMeow {
    /*
    I hate kotlin so much this is why I stole code from discord I cannot with this language
    Why is everything a val but sometimes not ? Why does the type go after the name ? ? And WHY DOES EVERYTHING HAVE TO BE SO FUN ? ?
    NO I DON'T WANT TO BE PART OF YOUR PRIVATE FUN ! ! :<
    */
    fun sendMessage(channelId:Long, content:String) {

        val params = RestAPIParams.Message(
            content, // Content
            NonceGenerator.computeNonce(ClockFactory.get()).toString(), // Nonce
            null, // ApplicationId
            null, // Activity
            emptyList(), // stickerIds
            null, // messageReference
            null, // TTS
            "", // embeds
            "" // components
        )

        val observable = RestAPI.api.sendMessage(channelId, params)

        Utils.threadPool.execute() {
            observable.subscribe(object : Subscriber<Message>() {
                override fun onCompleted() {
                }

                override fun onError(th: Throwable) {
                    Utils.showToast("$th \n[neutered] Please report this error ! :3", showLonger = true)
                    th.printStackTrace()
                }

                override fun onNext(msg: com.discord.api.message.Message) {
                }
            })
        }
    }
}
