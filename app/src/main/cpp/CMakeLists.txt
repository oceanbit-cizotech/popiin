cmake_minimum_required(VERSION 3.18.1)
project("popiin_native")

add_library(native-lib SHARED native-lib.cpp)

find_library(log-lib log)
target_link_libraries(native-lib ${log-lib})
