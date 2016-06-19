#include<stdio.h>
#include<string.h>

// return 1 if we should continue, 0 if we should stop
int processOneLine(){
	char s[1024];
	char *s1,*s2;
	int isPalindrome=1; // it is a palindrome until we find out otherwise
	s1=s;
	int sentinel=0;
	//Only read the first 1023 characters from the input string, then add a null terminator
	while(sentinel <1024){
		*s1=getchar();
		if(*s1=='\n')*s1='\0';
		if(*s1=='\0')break;
		s1++;
		sentinel++;
	}
	if(s[1023] != '\0'){
		s[1023] = '\0';
	}
	s2=s;
	s1--;
	while(s2<s1){
		if(*s1!=*s2){
			isPalindrome=0;
			break;
		}
		*s1--;
		*s2++;
	}
	if(isPalindrome){
		printf("Your string is a palindrome\n");
	} else {
		printf("Your string is not a palindrome\n");
	}
	fflush(stdout);
	if(strncmp(s,"quit",4)==0)return 0;
	return 1;
}

int main(int argc, char **argv){
	printf("Palindrome server, 'quit' to quit:\n");
	fflush(stdout);
	while(1){
		if(!processOneLine())break;
	}	
	return(0);
}
