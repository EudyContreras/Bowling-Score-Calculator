package com.eudycontreras.bowlingcalculator.adapters

import com.eudycontreras.bowlingcalculator.calculator.elements.Frame
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameLast.Companion.FRAME_LAST
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal
import com.eudycontreras.bowlingcalculator.calculator.elements.FrameNormal.Companion.FRAME_NORMAL
import com.google.gson.*
import java.lang.reflect.Type


/**
 * @Project BowlingCalculator
 * @author Eudy Contreras.
 */

class FrameTypeAdapter : JsonDeserializer<Frame>, JsonSerializer<Frame>{

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Frame? {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get(Frame.TYPE_KEY)

        if (type != null) {
            when (type.asString) {
                FRAME_NORMAL -> return context.deserialize<FrameNormal>(
                    jsonObject,
                    FrameNormal::class.java
                )
                FRAME_LAST -> return context.deserialize<FrameLast>(
                    jsonObject,
                    FrameLast::class.java
                )
            }
        }
        return null
    }

    override fun serialize(frame: Frame, type: Type, context: JsonSerializationContext): JsonElement {
        return if (frame is FrameNormal) {
            context.serialize(frame, FrameNormal::class.java)
        } else {
            context.serialize(frame, FrameLast::class.java)
        }
    }
}