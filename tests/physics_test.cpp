#include <cassert>
#include "../app/src/main/cpp/landing.hpp"

int main() {
    const auto centered = citybloxx::land(20.0F, 80.0F, 20.0F, 80.0F);
    assert(centered.successful && centered.left == 20.0F && centered.width == 80.0F);

    const auto clipped = citybloxx::land(70.0F, 50.0F, 20.0F, 80.0F);
    assert(clipped.successful && clipped.left == 70.0F && clipped.width == 30.0F);

    const auto missed = citybloxx::land(110.0F, 20.0F, 20.0F, 80.0F);
    assert(!missed.successful && missed.width == 0.0F);
}
