# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.10

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native

# Include any dependencies generated for this target.
include CMakeFiles/wordcount-part.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/wordcount-part.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/wordcount-part.dir/flags.make

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o: CMakeFiles/wordcount-part.dir/flags.make
CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src/main/native/examples/impl/wordcount-part.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src/main/native/examples/impl/wordcount-part.cc

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src/main/native/examples/impl/wordcount-part.cc > CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.i

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src/main/native/examples/impl/wordcount-part.cc -o CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.s

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.requires:

.PHONY : CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.requires

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.provides: CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.requires
	$(MAKE) -f CMakeFiles/wordcount-part.dir/build.make CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.provides.build
.PHONY : CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.provides

CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.provides.build: CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o


# Object files for target wordcount-part
wordcount__part_OBJECTS = \
"CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o"

# External object files for target wordcount-part
wordcount__part_EXTERNAL_OBJECTS =

examples/wordcount-part: CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o
examples/wordcount-part: CMakeFiles/wordcount-part.dir/build.make
examples/wordcount-part: libhadooppipes.a
examples/wordcount-part: libhadooputils.a
examples/wordcount-part: /usr/lib/x86_64-linux-gnu/libssl.so
examples/wordcount-part: /usr/lib/x86_64-linux-gnu/libcrypto.so
examples/wordcount-part: CMakeFiles/wordcount-part.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable examples/wordcount-part"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/wordcount-part.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/wordcount-part.dir/build: examples/wordcount-part

.PHONY : CMakeFiles/wordcount-part.dir/build

CMakeFiles/wordcount-part.dir/requires: CMakeFiles/wordcount-part.dir/main/native/examples/impl/wordcount-part.cc.o.requires

.PHONY : CMakeFiles/wordcount-part.dir/requires

CMakeFiles/wordcount-part.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/wordcount-part.dir/cmake_clean.cmake
.PHONY : CMakeFiles/wordcount-part.dir/clean

CMakeFiles/wordcount-part.dir/depend:
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/src /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-tools/hadoop-pipes/target/native/CMakeFiles/wordcount-part.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/wordcount-part.dir/depend

