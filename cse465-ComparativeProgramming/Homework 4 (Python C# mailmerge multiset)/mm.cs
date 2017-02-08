using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSE465
{
    class mm
    {
        public const char DELIM = '\t';
        public static string merge(String tmpl, string[] fields, string[] replacements)
        {
            for (int i = 0; i < fields.Length; i++) {
                tmpl = tmpl.Replace("<<" + fields[i] + ">>", replacements[i]);
            }
            return tmpl;
        }
        public static void save(string filename, string text)
        {
            StreamWriter output = new StreamWriter(filename + ".txt");
            output.Write(text);
            output.Close();
        }
        static void Main(string[] args)
        {
            // Load the template
            StreamReader input = new StreamReader(args[args.Length-1]);
            string tmpl = input.ReadToEnd();
            input.Close();

            // Load the data file
            input = new StreamReader(args[args.Length-2]);

            //Get the fields
            string[] fields = input.ReadLine().Split(DELIM);

            string line, temp;
            string[] data;
            while((line = input.ReadLine()) != null)
            {
                data = line.Split(DELIM);
                temp = merge(tmpl, fields, data);
                save(data[1], temp);
            }
        }
    }
}
