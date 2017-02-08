class Random {
public static void main(String[] args) {
double[] doubles = new double[1000000];
for(int i = 0; i < 1000000; i++)
doubles[i] = Math.random();

double sum = 0;

for(int i =0; i < 1000000; i++)
sum += doubles[i];

System.out.println(sum);
}
}
