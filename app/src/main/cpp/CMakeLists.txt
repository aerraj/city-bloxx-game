cmake_minimum_required(VERSION 3.22.1)
project(citybloxx LANGUAGES CXX)

add_library(citybloxx SHARED native-lib.cpp)
target_compile_features(citybloxx PRIVATE cxx_std_20)
