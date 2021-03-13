package src.utils;


// Java program to rotate an array by
// d elements

public class RotateArrays<T> {
    /*Function to left rotate arr[] of size n by d*/
    public void leftRotate(T[] arr, int d, int n)
    {
        for (int i = 0; i < d; i++)
            leftRotateByOne(arr, n);
    }

    void leftRotateByOne(T[] arr, int n)
    {
        int i;
        T temp;
        temp = arr[0];
        for (i = 0; i < n - 1; i++)
            arr[i] = arr[i + 1];
        arr[i] = (T) temp;
    }

    /*Function to right rotate arr[] of size n by d*/
    public void rightRotate(T[] arr, int d, int n)
    {
        for (int i = 0; i < d; i++)
            rightRotateByOne(arr, n);
    }

    void rightRotateByOne(T[] arr, int n)
    {
        int i; T temp;
        temp = arr[n-1];
        for (i = n-1; i > 0; i--)
            arr[i] = arr[i - 1];
        arr[i] = (T) temp;
    }


    /* utility function to print an array */
    public void printArray(T[] arr, int n)
    {
        for (int i = 0; i < n; i++)
            System.out.print(arr[i] + " ");
    }

    // function that rotates s towards left by d
    public String leftRotate(String str, int d)
    {
        return str.substring(d) + str.substring(0, d);
    }

    // function that rotates s towards right by d
    public String rightRotate(String str, int d)
    {
        return leftRotate(str, str.length() - d);
    }

    public void swap(T[] arr, int i, int j){
        T aux = arr[i];
        arr[i] = arr[j];
        arr[j] = aux;
    }

    // Driver program to test above functions
    public static void main(String[] args)
    {
        //Usage examples
        Byte[] arr = { 1, 2, 3, 4, 5, 6, 7 };
        RotateArrays<Byte> rotate = new RotateArrays<>();
        rotate.rightRotate(arr, 1, 7);
        rotate.swap(arr,2,3);
        rotate.printArray(arr, 7);
    }
}

