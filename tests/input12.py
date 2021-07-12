def times_2(array):
    new_array = [i*2 for i in array] 
    return new_array



if __name__ == "__main__":
    x = [1,2,3,4,5]
    y = [i*2 for i in x]
    print(f'The final sum is: {sum(y)}')