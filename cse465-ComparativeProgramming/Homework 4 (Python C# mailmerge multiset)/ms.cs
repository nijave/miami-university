using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CSE465
{
    // Implements a multiset similar to C++ multiset. Your implementation should
    // operator properly for all of the basic types, int, double, string, etc.
    // Efficiency of your code should easily allow 100's of items to be insert,
    // without restrictions on the values of those items.
    public class Multiset<T> : IEnumerable
    {
        private List<T> elements;
        private List<int> counts;

        // Constructor for multiset.
        public Multiset()
        {
            elements = new List<T>();
            counts = new List<int>();
        }
        // Adds a single instance of v to the multiset.
        public void Add(T v)
        {
            int pos = elements.IndexOf(v);
            if (pos >= 0)
            {
                counts[pos]++;
            }
            else
            {
                elements.Add(v);
                counts.Add(1);
            }
        }
        // Removes a single instance of v to the multiset. If the
        // value v is not a member of the multiset, no action is
        // performed.
        public void Remove(T v)
        {
            // Try to find position
            int pos = elements.IndexOf(v);

            // Decrement count
            if (pos >= 0)
            {
                counts[pos]--;

                // Remove the element and count if zero
                if (counts[pos] <= 0)
                {
                    elements.RemoveAt(pos);
                    counts.RemoveAt(pos);
                }
            }
        }
        // Returns the number of copies of the value v are present
        // in the multiset.
        public int Count(T v)
        {
            int cnt;
            int pos = elements.IndexOf(v);

            if (pos >= 0)
                cnt = counts[pos];
            else
                cnt = 0;

            return cnt;
        }
        // Returns the total number of items in the multiset.
        public int Size()
        {
            return counts.Sum();
        }

        public IEnumerator GetEnumerator()
        {
            for (int i = 0; i < elements.Count; i++)
                for(int j = 0; j < counts[i]; j++)
                    yield return elements[i];
        }
    }
    public class Program
    {
        public static void TestIntMS()
        {
            Multiset<int> intMS = new Multiset<int>();

            intMS.Add(3);
            intMS.Add(3);
            intMS.Add(4);
            intMS.Add(3);
            intMS.Add(4);
            intMS.Add(7);
            Console.WriteLine("*********************");
            Console.WriteLine(intMS.Size());
            for (int i = 0; i < 10; i++)
            {
                Console.WriteLine("{0} {1}", i, intMS.Count(i));
            }
            intMS.Remove(3);
            intMS.Remove(3);
            intMS.Remove(3);
            intMS.Remove(3);
            intMS.Remove(3);
            Console.WriteLine("*********************");
            Console.WriteLine(intMS.Size());
            for (int i = 0; i < 10; i++)
            {
                Console.WriteLine("{0} {1}", i, intMS.Count(i));
            }
        }
        public static void TestStringMS()
        {
            Multiset<string> strMS = new Multiset<string>();

            strMS.Add("3");
            strMS.Add("3");
            strMS.Add("4");
            strMS.Add("3");
            strMS.Add("4");
            strMS.Add("7");
            Console.WriteLine("*********************");
            Console.WriteLine(strMS.Size());
            for (int i = 0; i < 10; i++)
            {
                string str = i.ToString();
                Console.WriteLine("{0} {1}", i, strMS.Count(str));
            }
            strMS.Remove("3");
            strMS.Remove("3");
            strMS.Remove("3");
            strMS.Remove("3");
            strMS.Remove("3");
            strMS.Remove("3");
            Console.WriteLine("*********************");
            Console.WriteLine(strMS.Size());
            for (int i = 0; i < 10; i++)
            {
                string str = i.ToString();
                Console.WriteLine("{0} {1}", i, strMS.Count(str));
            }
        }
        public static void Main(string[] args)
        {
            TestIntMS();
            TestStringMS();
        }
    }
}
