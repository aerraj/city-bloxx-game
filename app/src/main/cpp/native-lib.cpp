#include <jni.h>

#include "landing.hpp"

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_aerraj_citybloxx_GameView_nativeLand(JNIEnv* env, jclass,
                                               jfloat movingLeft, jfloat movingWidth,
                                               jfloat targetLeft, jfloat targetWidth) {
    const citybloxx::Landing result = citybloxx::land(
            movingLeft, movingWidth, targetLeft, targetWidth);
    const jfloat values[] = {
            result.left,
            result.width,
            result.successful ? 1.0F : 0.0F,
    };
    jfloatArray output = env->NewFloatArray(3);
    env->SetFloatArrayRegion(output, 0, 3, values);
    return output;
}
