.data
	nums:		.space 40	#array of 10 ints
	newHistogram:	.asciiz	"1. New Histogram\n"
	printHistogram:	.asciiz	"2. Print Histogram\n"
	quit:		.asciiz	"3. Quit\n"
	invalidMenu:	.asciiz	"Invalid menu item entered\n"
	invalidNum:	.asciiz "Numbers must be in the range 0-50\n"
	prompt:		.asciiz	"Please enter 10 integers from 0 to 50\n"
	
## Print the menu
.macro printMenu()
	li	$v0, 4			#setup to print a string
	la	$a0, newHistogram	#menu item 1
	syscall				#print
	la	$a0, printHistogram	#menu item 2
	syscall				#print
	la	$a0, quit		#menu item 3
	syscall				#print
.end_macro

## Exit
.macro exit()
	li	$v0, 10			#set to exit
	syscall				#exit
.end_macro

##Get number frequency
.macro	freq($x)
	move	$t0, $x		#number to check for
	li	$v0, 0		#frequency of number
	li	$t1, 0		#loop counter
	freqLoop:
		beq	$t1, 44, freqLoopEnd	#end after 10 ints
		lw	$t2, nums($t1)		#load int into t2
		add	$t1, $t1, 4		#increment $t1 by 4
		bne	$t0, $t2, freqLoop	#go to top of loop if not equal
		add	$v0, $v0, 1		#increment frequency if equal
		b	freqLoop		#restart loop
	freqLoopEnd:
.end_macro

## Print n asterisks
.macro	printBar($n)
	move 	$t0, $n		#store the number of runs in $t0
	li	$v0, 11
	li	$a0, 42
	printBarLoop:
		blez	$t0, printBarLoopEnd
		syscall
		add	$t0, $t0, -1
		b 	printBarLoop
	printBarLoopEnd:
.end_macro

## Prints a string
.macro print_str($str)
	.data
		strPrint_addr:	.asciiz	$str
	.text
		li	$v0, 4	#print string
		la	$a0, strPrint_addr
		syscall
.end_macro

.text
	j main				#go to menu to start out
	invalid:
		li	$v0, 4			#setup to print a string
		la	$a0, invalidMenu	#print invalid menu item string
		syscall
	main:
		printMenu()		#print the menu
		
		li	$v0, 5		#read int
		syscall			#v0 will contain entered int
		
		beq	$v0, 1, new	#go to new
		beq	$v0, 2, print	#go to print
		bne	$v0, 3, invalid	#invalid menu item entered
		
		exit()
	
	new:
		la	$t0, nums	## load address of the nums array
		li	$t9,0		## set a counter for how many times user have inputed 
		la	$a0,prompt
		li	$v0,4
		syscall
		b newLoop		#skip over the invalid for the first time around
		newLoopInvalid:
			li	$v0, 4			#setup to print a string
			la	$a0, invalidNum		#print invalid menu item string
			syscall
		newLoop:
			li	$v0,5			## read int 
			syscall 
			move	$t1,$v0			## set t1 to the int
			bltz	$t1, newLoopInvalid	#branch if below zero
			add	$t2, $t1, -50
			bgtz	$t2, newLoopInvalid	#branch if greater than 0 (num-50)
			sb	$t1,($t0)		## store the bytes in the array - at t0
			addi	$t0,$t0,4		## add 4 to the array address
			add	$t9,$t9,1		## bump the counter up by one
			blt	$t9,10,newLoop 		## while the counter is less than 10, loop it 
		
		b	main
	
	print:
		li	$s0, 0		#start $s0 at 0
		
		printLoop:
			freq($s0)
			move	$t9, $v0		#move frequency to t9
			li	$v0, 1			#set to print int
			move	$a0, $s0		#print current number
			syscall
			print_str(" ")				
			printBar($t9)			#create a bar of $t0 length
			print_str("\n")
			add	$s0, $s0, 1
			bne	$s0, 51, printLoop	#go through another iteration of the loop
		b 	main				#go back to main menu
