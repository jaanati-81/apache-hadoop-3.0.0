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
CMAKE_SOURCE_DIR = /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target

# Include any dependencies generated for this target.
include main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/depend.make

# Include the progress variables for this target.
include main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/progress.make

# Include the compile flags for this target's objects.
include main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs-tests/native_mini_dfs.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs-tests/native_mini_dfs.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs-tests/native_mini_dfs.c > CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs-tests/native_mini_dfs.c -o CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o


main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/common/htable.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/common/htable.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/common/htable.c > CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/common/htable.c -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o


main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/exception.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/exception.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/exception.c > CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/exception.c -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o


main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/jni_helper.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/jni_helper.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/jni_helper.c > CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/jni_helper.c -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o


main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/mutexes.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/mutexes.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/mutexes.c > CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/mutexes.c -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o


main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/flags.make
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o: /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/thread_local_storage.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building C object main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o   -c /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/thread_local_storage.c

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.i"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/thread_local_storage.c > CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.i

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.s"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs/os/posix/thread_local_storage.c -o CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.s

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.requires:

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.provides: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.requires
	$(MAKE) -f main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.provides.build
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.provides

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.provides.build: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o


# Object files for target native_mini_dfs
native_mini_dfs_OBJECTS = \
"CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o" \
"CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o" \
"CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o" \
"CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o" \
"CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o" \
"CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o"

# External object files for target native_mini_dfs
native_mini_dfs_EXTERNAL_OBJECTS =

main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build.make
main/native/libhdfs-tests/libnative_mini_dfs.a: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Linking C static library libnative_mini_dfs.a"
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && $(CMAKE_COMMAND) -P CMakeFiles/native_mini_dfs.dir/cmake_clean_target.cmake
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/native_mini_dfs.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build: main/native/libhdfs-tests/libnative_mini_dfs.a

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/build

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/native_mini_dfs.c.o.requires
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/common/htable.c.o.requires
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/exception.c.o.requires
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/jni_helper.c.o.requires
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/mutexes.c.o.requires
main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires: main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/__/libhdfs/os/posix/thread_local_storage.c.o.requires

.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/requires

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/clean:
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests && $(CMAKE_COMMAND) -P CMakeFiles/native_mini_dfs.dir/cmake_clean.cmake
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/clean

main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/depend:
	cd /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/src/main/native/libhdfs-tests /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests /home/hduser/Documents/Our_Project/hadoop-3.0.0-src/hadoop-hdfs-project/hadoop-hdfs-native-client/target/main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : main/native/libhdfs-tests/CMakeFiles/native_mini_dfs.dir/depend

