An explanation of the vulnerability in swapAround, leading to a buffer overrun. As well a description of how to exploit the vulnerability, launching a shell:

Yes, there’s a buffer overrun vulnerability in swapAround().c.
Using strcpy to write to local buffer str can cause str to overflow. This is achieved by having str2 fully filled with non-NULL characters.

The way it happens is that, the statement srtcpy(str, str2)  in the swapAround() function copies all the content of str2 (128 bytes of non_null characters) into str, but it doesn’t stop there because there is no null terminator in str2, so it  moves on to copy the content of str1 into str overflowing str. This happens because str2 is placed right above str1 in the stack frame of swapAround() function.

How we were able to launch the sh shell by overflowing local variable str:
The way this was done is by placing the exec code responsible for execing the shell sh inside str2 from a2, padded  by noops bytes from above and much less noops from below. Multiple copies of a return address to a location inside str (closer to the top of str) was placed in str1 followed by  a NULL terminator ( this prevents segmentation faulting when executing strcpy(str, str2);).

Then, when executing strcpy(str, str2); the following happens:
All the 128 bytes inside str2 including our shell_launching code is placed inside str. Strcpy doesn’t stop though at the end of str2, it goes on to copy from str1 and this overwrites the return address RET in stack frame of swapAround() replacing it with the return address placed in str1 (points to a location in str).

Next, when swapAround() tries to return, control jumps to a location in str and starts executing from there. Along that, our shell-launching code is executed causing sh to run.


