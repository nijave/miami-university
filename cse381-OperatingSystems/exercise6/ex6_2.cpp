
#include <iostream>
#include <thread>
#include <vector>
#include <string>
#include <cmath>
#include <algorithm>
#include <iterator>
#include <fstream>
#include <functional>

// Forward declarations to make the compiler happy
bool isPrime(const long);
void primeCheck(const std::vector<long>& numbers,
                std::vector<bool>& isPrimeList, int startIdx, int endIdx);

/** Method to launch threads for data parallel check for prime
    numbers.

    \param[in] numberList The list of numbers to checked to see if it
    is prime. This list may have millions of numbers in it.

    \param[out] isPrimeList A list of boolean values such that
    isPrimeList[i] indicates if the value in numberList[i] is
    prime. This value is to be cmputed using multiple threads.

    \param[in] thrCount The number of threads to be created by this
    method.

    \return This method returns a vector containing the threads
    created by this method.
*/
std::vector<std::thread>
createThreads(const std::vector<long>& numberList,
              std::vector<bool>& isPrimeList, const int thrCount) {
    
    std::vector<std::thread> threads(thrCount);
//printf("numbers: %i\n", numberList.size());
    int nums = numberList.size();

    for(int i = 0; i < thrCount; i++) {
	int start = i + i*nums/thrCount;
	int end = start + nums/thrCount;
	printf("start: %i, end: %i\n", start, end);
	if(end-1 >= nums)
	    end = nums-1;
	threads.push_back(std::thread (primeCheck, std::ref(numberList), std::ref(isPrimeList), start, end));
    }

    return threads;
}

//-------------------------------------------------------------
//  DO  NOT  MODIFY  CODE  BELOW  THIS  LINE
//-------------------------------------------------------------

/**
 * The thread method.
 * 
 * This method iterates over the given range of values to determine a
 * number is prime.
 *
 */
void primeCheck(const std::vector<long>& numbers,
                std::vector<bool>& isPrimeList, int startIdx, int endIdx) {
    std::transform(numbers.cbegin() + startIdx, numbers.cbegin() + endIdx,
                   isPrimeList.begin() + startIdx, isPrime);
}

/**
   Convenience method to load integers from a given file.

   \param[in] filePath The path to the file from where the numbers are
   to be loaded.
*/
std::vector<long> loadData(const std::string& filePath) {
    // Load numbers from a given text file.
    std::ifstream dataFile(filePath);
    std::istream_iterator<long> readIter(dataFile), eof;
    std::vector<long> numberList(readIter, eof);
    return numberList;
}


bool isPrime(const long value) {
    int factorCount     = 0;
    const int MaxFactor = (std::sqrt(value) + 1);
    for (int i = 2; (i < MaxFactor); i++) {
        if (value % i == 0) {
            factorCount++;
        }
    }
    return (factorCount == 0);
}

// A simple error message:
const std::string INSUFFICIENT_ARGS =
    "Error: Insufficient Arguments.\nThe program requires the following " \
    "arguments:\n    1. The file with numbers to be processed.\n" \
    "    2. The number of threads to be used for processing.\n" \
    "EXAMPLE: ./ex6_2 numbers.txt 3\n";


/**
 * The main method that fires off various threads and waits for them
 * to finish.
 *
 * @param args Optional command-line arguments. The first argument is
 * the number of threads. The second argument is the time delay.
 */
int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << INSUFFICIENT_ARGS;
        return 1;
    }
    // Load the data from the file.
    std::vector<long> numberList = loadData(argv[1]);
    std::vector<bool> isPrimeList(numberList.size());

    // Create the specified number of threads to process numbers with
    // suitable parameters.
    std::vector<std::thread> thrList =
        createThreads(numberList, isPrimeList, std::stoi(argv[2]));
    // Wait for the threads to join.
    std::for_each(thrList.begin(), thrList.end(),
                  std::mem_fun_ref(&std::thread::join));
    // Print the results
    for (size_t i = 0; (i < numberList.size()); i++) {
        std::cout << numberList[i] << ": "
                  << (isPrimeList[i] ? "is " : "not ")
                  << "prime" << std::endl;
    }
    return 0;
}
