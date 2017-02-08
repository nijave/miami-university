
#include <unistd.h>
#include <iostream>
#include <thread>
#include <algorithm>
#include <vector>
#include <functional>

int sleepDur;
int numThreads;

// Forward declaration for method defined further below
void timer(const int MaxTime, const int threadID);
void printOutPid(int);

/**
 * The main method that launched various threads and waits for them to
 * finish.
 * 
 * @param args Optional command-line arguments. The first argument is
 * the number of threads. The second argument is the time delay.
 */
int main(int argc, char *argv[]) {
    // Process command-line arguments (if any) or assume default values.
    // Implement the desired functionality of the main method
    sleepDur = 2400;
    numThreads = 1;

    if(argc >= 2)
    	numThreads = strtol(argv[1], NULL, 10);
    if(argc == 3)
    	sleepDur = strtol(argv[2], NULL, 10);

    printf("C++ process PID: %i\n", (int)getpid());
    printf("Starting up %i threads.\n", numThreads);

    std::thread th[numThreads];

    for (int i = 0; i < numThreads; i++) {
	th[i] = std::thread (timer,sleepDur,i);
//	printf("Create thread %i\n", i);
    }

    for (int i = 0; i < numThreads; i++) {
	th[i].join();
    }

    return 0;
}

void printOutPid(int number){
    std::cout<<("%d\n",getpid());
}



// ------------------------------------------------------------------
//         DO  NOT  MODIFY  CODE  BELOW  THIS  LINE
// ------------------------------------------------------------------

/**
 * The thread method.
 * 
 * This method simply sleeps for 1 second and prints the time
 * remaining until the specified MaxTime (seconds) has elapsed.
 *
 * \param[in] MaxTime The maximum number of seconds this thread must
 * sleep.
 *
 * \param[in] threadID A logical index number for the thread.
 */
void timer(const int MaxTime, const int threadID) {
    for (int i = MaxTime; (i > 0); i--) {
        if (threadID == 0) {
            std::cout << "\r" << i << " seconds left..." << std::flush;
        }
        std::chrono::milliseconds duration(1000);
        std::this_thread::sleep_for(duration);
    }
}

// End of source code
