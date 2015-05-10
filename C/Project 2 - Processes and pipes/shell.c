#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <mcheck.h>

#include "parser.h"
#include "shell.h"

/**
 * Program that simulates a simple shell.
 * The shell covers basic commands, including builtin commands 
 * (cd and exit only), standard I/O redirection and piping (|). 
 
 */

#define MAX_DIRNAME 100
#define MAX_COMMAND 1024
#define MAX_TOKEN 128

/* Functions to implement, see below after main */
int execute_cd(char** words);
int execute_nonbuiltin(simple_command *s);
int execute_simple_command(simple_command *cmd);
int execute_complex_command(command *cmd);

int child;
int childpid;
int parentpid;


int main(int argc, char** argv) {
	
	char cwd[MAX_DIRNAME];           /* Current working directory */
	char command_line[MAX_COMMAND];  /* The command */
	char *tokens[MAX_TOKEN];         /* Command tokens (program name, 
					  * parameters, pipe, etc.) */

	while (1) {

		/* Display prompt */		
		getcwd(cwd, MAX_DIRNAME-1);
		
		/*if cwd is root then just promt*/
		if(strcmp(cwd,"/")==0){
			printf("%s> ", cwd);
		}
		/*otherwise append '/' to add
		last directory to prompt*/
		else{
			strcat(cwd,"/");
			printf("%s> ", cwd);
		}
		
		/* Read the command line */
		fgets(command_line, MAX_COMMAND, stdin);
		/* Strip the new line character */
		if (command_line[strlen(command_line) - 1] == '\n') {
			command_line[strlen(command_line) - 1] = '\0';
		}
		
		/* Parse the command into tokens */
		parse_line(command_line, tokens);

		/* Check for empty command */
		if (!(*tokens)) {
			continue;
		}
		
		/* Construct chain of commands, if multiple commands */
		command *cmd = construct_command(tokens);
		//print_command(cmd, 0);
    
		int exitcode = 0;
		if (cmd->scmd) {
			exitcode = execute_simple_command(cmd->scmd);
			if (exitcode == -1) {
				break;
			}
		}
		else {
			exitcode = execute_complex_command(cmd);
			if (exitcode == -1) {
				break;
			}
		}
		release_command(cmd);
	}
    
	return 0;
}


/**
 * Changes directory to a path specified in the words argument;
 * For example: words[0] = "cd"
 *              words[1] = "csc209/assignment3/"
 * Your command should handle both relative paths to the current 
 * working directory, and absolute paths relative to root,
 * e.g., relative path:  cd csc209/assignment3/
 *       absolute path:  cd /u/bogdan/csc209/assignment3/
 */
int execute_cd(char** words) {
	
	/** 
	 * TODO: 
	 * The first word contains the "cd" string, the second one contains 
	 * the path.
	 * Check possible errors:
	 * - The words pointer could be NULL, the first string or the second 
	 *   string could be NULL, or the first string is not a cd command
	 * - If so, return an EXIT_FAILURE status to indicate something is 
	 *   wrong.
	 */

	 /**
	 * TODO: 
	 * The safest way would be to first determine if the path is relative 
	 * or absolute (see is_relative function provided).
	 * - If it's not relative, then simply change the directory to the path 
	 * specified in the second word in the array.
	 * - If it's relative, then make sure to get the current working 
	 * directory, append the path in the second word to the current working
	 * directory and change the directory to this path.
	 * Hints: see chdir and getcwd man pages.
	 * Return the success/error code obtained when changing the directory.
	 */

	 int result= EXIT_FAILURE; 

	 /*if words pointer is not null and
	 and first element of words is cd*/
	 if(words != NULL && strcmp(words[0],"cd")==0){ 

	 	char absPath[MAX_DIRNAME]; //to store cwd
		getcwd(absPath, MAX_DIRNAME-1); //obtain cwd
		//strcat(absPath,"/"); //append '/' at the end of absPath
	 	
	 	/*if no path is given*/
	 	if (words[1]==NULL)
	 	{
			result = chdir(absPath); //execute with absolute path
			return result; //returning success/error code
	 	}

	 	/*if path is given*/
		else{ 

			// if path is absolute
		 	if(is_relative(words[1]) == 0){
		 		result =  chdir(words[1]); //executing the command
		 		return result;	//returning success/error code
		 	}

		 	//if command is cd but path is relative
		 	else if (is_relative(words[1]) == 1){
		 		strcat(absPath,words[1]); //append absPath with path given
				result = chdir(absPath); //execute with absolute path
				return result; //returning success/error code
		 	}
		 }

	}
	 return result; //return EXIT_FAILURE if command is not cd
}


/**
 * Executes a program, based on the tokens provided as 
 * an argument.
 * For example, "ls -l" is represented in the tokens array by 
 * 2 strings "ls" and "-l", followed by a NULL token.
 * The command "ls -l | wc -l" will contain 5 tokens, 
 * followed by a NULL token. 
 */
int execute_command(char **tokens) {

	/**
	 * TODO: execute a program, based on the tokens provided.
	 * The first token is the command name, the rest are the arguments 
	 * for the command. 
	 * Hint: see execlp/execvp man pages.
	 * 
	 * - In case of error, make sure to use "perror" to indicate the name
	 *   of the command that failed.
	 *   You do NOT have to print an identical error message to what would 
	 *   happen in bash.
	 *   If you use perror, an output like: 
	 *      my_silly_command: No such file of directory 
	 *   would suffice.
	 * Function returns only in case of a failure (EXIT_FAILURE).
	 */

	/*call to exec to execute user command*/
	int exec_status = execvp(tokens[0],tokens); 

	if(exec_status == -1){ //checking return value by exec
		perror(tokens[0]); //if exec call fails
		return EXIT_FAILURE; //returns when exec fails
	}
	return 0;
}


/**
 * Executes a non-builtin command.
 */
int execute_nonbuiltin(simple_command *s) {
	/**
	 * TODO: Check if the in, out, and err fields are set (not NULL),
	 * and, IN EACH CASE:
	 * - Open a new file descriptor (make sure you have the correct flags,
	 *   and permissions);
	 * - redirect stdin/stdout/stderr to the corresponding file.
	 *   (hint: see dup2 man pages).
	 * - close the newly opened file descriptor in the parent as well. 
	 *   (Avoid leaving the file descriptor open across an exec!) 
	 * - finally, execute the command using the tokens (see execute_command
	 *   function above).
	 * This function returns only if the execution of the program fails.
	 */

	int stdin_fd =0; //for redirection of standard input
	int stdout_fd = 0; //for redirection of standard output
	int stderr_fd = 0; //for redirection of standard error

	/*if standard input redirection is requested*/
	if(s->in != NULL){
		/*opening file descriptor to accommodate
		standard input redirection*/
		stdin_fd = open(s->in, O_RDONLY, S_IRUSR);
		
		/*check if open call was successful*/
		if(stdin_fd < 0){
			perror("open file descriptor input\n");
			return -1;
		}
		/*close standard input file descriptor*/
		close(STDIN_FILENO);

		/*redirect standard input to stdin_fd*/
		dup2(stdin_fd,STDIN_FILENO);

		/*close newly opened file descriptor*/
		close(stdin_fd);
	}

	/*if standard output redirection is requested*/
	if(s->out != NULL){
		/*opening file descriptor to accommodate
		standard output redirection*/
		stdout_fd = open(s->out, O_WRONLY | O_CREAT, S_IWUSR);
		
		/*check if open call was successful*/
		if(stdout_fd < 0){
			perror("open file descriptor output\n");
			return -1;
		}

		/*close standard output file descriptor*/
		close(STDOUT_FILENO);

		/*redirect standard output to stdout_fd*/
		dup2(stdout_fd,STDOUT_FILENO);

		/*close newly opened file descriptor*/
		close(stdout_fd);

	}

	/*if standard err redirection is requested*/
	if(s->err != NULL){

		/*opening file descriptor to accommodate
		standard err redirection*/
		stderr_fd = open(s->err, O_WRONLY | O_CREAT, S_IWUSR);

		/*check if open call was successful*/
		if(stderr_fd < 0){
			perror("open file descriptor error\n");
			return -1;
		}

		/*close standard output file descriptor*/
		close(STDERR_FILENO);

		/*redirect standard output to stdout_fd*/
		dup2(stderr_fd,STDERR_FILENO);

		/*close newly opened file descriptor*/
		close(stderr_fd);
	}

	/*Execute entered command*/
	int result = execute_command(s->tokens);

	/*return only if executiong of command fails*/
	if(result == EXIT_FAILURE){
		return result;
	}
	return 0;
}


/**
 * Executes a simple command (no pipes).
 */
int execute_simple_command(simple_command *cmd) {
	
	/**
	 * TODO: 
	 * Check if the command is builtin.
	 * 1. If it is, then handle BUILTIN_CD (see execute_cd function provided) 
	 *    and BUILTIN_EXIT (simply exit with an appropriate exit status).
	 * 2. If it isn't, then you must execute the non-builtin command. 
	 * - Fork a process to execute the nonbuiltin command 
	 *   (see execute_nonbuiltin function above).
	 * - The parent should wait for the child.
	 *   (see wait man pages).
	 */

	int child_exit_status = 5;
	int status;
	
	/*check if command to execute is cd*/
	if(is_builtin(cmd->tokens[0]) == BUILTIN_CD){
		/*execute cd*/
		return execute_cd(cmd->tokens);
	}

	/*otherwise check if command to execute is exit*/
	else if(is_builtin(cmd->tokens[0]) == BUILTIN_EXIT){
		
		/*execute exit*/
		exit(0);
	}

	/*if command is not exit nor cd*/
	else if(is_builtin(cmd->tokens[0]) == 0) {

		/*forking to successfull execute command
		without exiting*/
		child = fork();

		/*check success/failue of fork*/
		if(child == -1){
			perror("fork unsuccessfull\n");
			return -1;
		}

		/*execute non-builtin command in child process
		while parent waits for child to finish 
		processing*/
		if(child == 0){
			/*execute command*/
			execute_nonbuiltin(cmd);

			/*send exit status to parent process*/
			exit(child_exit_status); 
		}
		wait(&status); //parent to wait for child	
	}
	return 0;
}


/**
 * Executes a complex command.  A complex command is two commands chained 
 * together with a pipe operator.
 */
int execute_complex_command(command *c) {
	
	/**
	 * TODO:
	 * Check if this is a simple command, using the scmd field.
	 * Remember that this will be called recursively, so when you encounter
	 * a simple command you should act accordingly.
	 * Execute nonbuiltin commands only. If it's exit or cd, you should not 
	 * execute these in a piped context, so simply ignore builtin commands. 
	 */

	/*if it is a simple command (i.e not NULL) and
	and command is not | (i.e pipe)*/
	if(c->scmd != NULL && strcmp(c->scmd->tokens[0],"|")!=0){

		/*execution of command - non-builtin commands only*/
		execute_nonbuiltin(c->scmd);
	}
	
	/** 
	 * Optional: if you wish to handle more than just the 
	 * pipe operator '|' (the '&&', ';' etc. operators), then 
	 * you can add more options here. 
	 */

	if (!strcmp(c->oper, "|")) {
		
		/**
		 * TODO: Create a pipe "pfd" that generates a pair of file 
		 * descriptors, to be used for communication between the 
		 * parent and the child. Make sure to check any errors in 
		 * creating the pipe.
		 */

		 /**
		 * TODO: Fork a new process.
		 * In the child:
		 *  - close one end of the pipe pfd and close the stdout 
		 * file descriptor.
		 *  - connect the stdout to the other end of the pipe (the 
		 * one you didn't close).
		 *  - execute complex command cmd1 recursively. 
		 * In the parent: 
		 *  - fork a new process to execute cmd2 recursively.
		 *  - In child 2:
		 *     - close one end of the pipe pfd (the other one than 
		 *       the first child), and close the standard input file 
		 *       descriptor.
		 *     - connect the stdin to the other end of the pipe (the 
		 *       one you didn't close).
		 *     - execute complex command cmd2 recursively. 
		 *  - In the parent:
		 *     - close both ends of the pipe. 
		 *     - wait for both children to finish.
		 */

		 /*for file descriptors to be opened.*/
		 int pfd[2];
		 
		 /*
		 *pid1, pid2 to distinguish between parent and child.
		 *child1, child2 stores child1 and child2 pids respectively.
		 */
		 int pid1, pid2, child1, child2;

		 /*
		 *child1status to store exit status of child1.
		 *child2status to store exit status of child2.
		 */
		 int child1status, child2status;

		 /*exit statuses of children*/
		 int child1_exit =6;
		 int child2_exit =7;

		 /*piping to facilitate communication between
		 *two processes.*/
		 int piped = pipe(pfd);

		 /*check if pipe was successfull or failed*/
		 if(piped == -1){
		 	perror("pipe");
		 	return -1;
		 }

		 /*calling fork and error check*/
		 pid1 = fork();
		 if(pid1 < 0){
		 	perror("fork");
		 	return -1;
		 }

		 /*child1 process*/
		 if(pid1 == 0){
		 	
		 	/*closing reading end of pipe*/
		 	close(pfd[0]);

		 	/*closing standard output*/
		 	close(STDOUT_FILENO);

		 	/*transferring wrting end of pipe
		 	to be standard out*/
		 	dup2(pfd[1],STDOUT_FILENO);

		 	/*execute command requested*/
		 	execute_complex_command(c->cmd1);

		 	/*send exit status to parent*/
		 	exit(child1_exit);
		 }

		 /*parent of child1 process*/
		 if(pid1 > 0){
		 	child1 = pid1; //storting child1 pid.

		 	/*calling fork and check for error.*/
		 	pid2 = fork();
		 	if(pid1 < 0){
		 		perror("fork");
		 		return -1;
		 	}
		 	/*child2 process*/
		 	if(pid2 == 0){

		 		/*closing writing end of pipe*/
		 		close(pfd[1]);

		 		/*closing standard input*/
		 		close(STDIN_FILENO);

		 		/*transferring reading end of pipe
		 		to be standard input*/
		 		dup2(pfd[0],STDIN_FILENO);

		 		/*execute command requested*/
		 		execute_complex_command(c->cmd2);

		 		/*send exit status to parent*/
		 		exit(child2_exit);
		 	}
			
			/*parent of child2 process*/
		 	if(pid2 > 0){
		 		child2 = pid2; //strong child2 pid
		 		close(pfd[1]); //closing piped writing end
		 		close(pfd[0]); //closing piped reading end

		 		//waiting for child 1 to exit.
		 		waitpid(child1, &child1status, 0);
		 		//waiting for child 2 to exit.
		 		waitpid(child2, &child2status, 0);
		 	}
		 }		 
	}
	return 0;
}