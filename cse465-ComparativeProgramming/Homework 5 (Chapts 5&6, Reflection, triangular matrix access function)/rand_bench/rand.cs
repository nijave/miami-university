using System;

namespace Ran
{
    class Program
    {
        static void Main(string[] args)
        {
            double[] doubles = new double[1000000];
            Random random = new Random();
            for (int i = 0; i < 1000000; i++)
                doubles[i] = random.NextDouble();
            double sum = 0;
            for (int i = 0; i < 1000000; i++)
                sum += doubles[i];
            Console.WriteLine(sum);
        }
    }
}

