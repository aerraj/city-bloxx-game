#pragma once

#include <algorithm>

namespace citybloxx {

struct Landing {
    float left;
    float width;
    bool successful;
};

inline Landing land(float movingLeft, float movingWidth,
                    float targetLeft, float targetWidth) {
    const float left = std::max(movingLeft, targetLeft);
    const float right = std::min(movingLeft + movingWidth, targetLeft + targetWidth);
    const float width = right - left;
    return {left, std::max(0.0F, width), width > 0.0F};
}

}  // namespace citybloxx
