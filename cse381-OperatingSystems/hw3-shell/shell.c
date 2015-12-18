/**
 * shell.c
 **/

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/stat.h>
#include "shell_tests.h"

#define MAX_LINE_LEN 256
#define MAX_ARGS (MAX_LINE_LEN/2)
#define WHITESPACE " \t\n"
#define PATH_SEPERATOR ":"
#define BUFFERSIZE 200

typedef struct {		/* Struct to contain a parsed command */
    char* name;
    int argc;
    char* argv[MAX_ARGS+1];
} command_t;

char* lastPath;

void printPrompt();
int exec_command();
int parse_command(char*, command_t*);
int cd(char*);
void help();
void dragon();

int main (int argc, char** argv) {
    /* Entry point for the testrunner program */
    /* DO NOT DELETE */
    if ((argc > 1) && !strcmp(argv[1], "-test")) {
        run_shell_tests(argc - 1, argv + 1);
        return EXIT_SUCCESS;
    }
    char buf[BUFFERSIZE];
    lastPath = getcwd(buf, sizeof(buf)); // initially set current working directory

    while(1) {
    	printPrompt();
    	exec_command();
    }

    return EXIT_SUCCESS;
}

void printPrompt() {
	fprintf(stdout, "Shell(pid=%i)> ", (int) getpid());
	fflush(stdout);
}

//Checks $PATH to see if the executable being called exists in the path
//Returns a 0 if found and updates command to include full path to executable
int findInPath(command_t *cmd) {
	int found = 0;
	char* dir;
	char command[BUFFERSIZE];
	struct stat buffer;
	char* path = malloc(strlen(getenv("PATH")) + 1);
	strcpy(path, getenv("PATH"));

	dir = strtok(path, PATH_SEPERATOR);

	while (dir) {
		//command = malloc(strlen(dir) + 1);
		strcpy(command, dir);
		strcat(command, "/");
		strcat(command, cmd->name);

		if(stat(command, &buffer) == 0) {
			free(cmd->name);
			free(cmd->argv[0]);
			cmd->name = malloc(strlen(command)+1);
			cmd->argv[0] = malloc(strlen(command)+1);
			strcpy(cmd->name, command);
			strcpy(cmd->argv[0], command);
			found = 1;
		}
		dir = strtok(NULL, PATH_SEPERATOR);
		//free(command);
		if(found == 1)
			break;
	}

	free(path);

	if(found == 1)
		return 0;
	else
		return 1;
}

//checks to see if a command exists
//checks to see if the command is in PATH
int cmd_exists(command_t *cmd) {
	struct stat buffer;

	if(stat(cmd->name, &buffer) == 0) {
		return 0;
	}
	else if (findInPath(cmd) == 0) {
		return 0;
	}
	return -1;
}

/* Frees dynamically allocated strings from a command. */
void free_command(command_t *cmd) {
    int i;
    for (i=0; ((i < cmd->argc) && (cmd->argv[i] != NULL)); i++) {
        free(cmd->argv[i]);
    }
    free(cmd->name);
}

//handles commands that are built in to the shell
int builtin_cmd(command_t *cmd) {
	char* word = cmd->name;
	
	if (strcmp(word,"exit") == 0){
		//kill(0, SIGKILL);
		exit(0);
	}
	else if (strcmp(word,"cd") == 0){
	  //Call cd function somehow with the path.
		if(cmd->argc == 1)
			cd(getenv("HOME"));
		else if (cmd->argc == 2)
			cd(cmd->argv[1]);
		else
			fprintf(stderr, "cd: Too many arguments\r\n");
	}
	else if (strcmp(word,"dragon") == 0){
		dragon();
	}
	else if (strcmp(word,"help") == 0){
		help();
	}
	else {
		return -1; // not a built in command
	}
	return 0;
}

//gets a command from stdin and tries to execute it
int exec_command() {
	command_t command;
	pid_t pid;
	int status;
	char cmdline[MAX_LINE_LEN];
	char* fgets_handle;

	fgets_handle = fgets(cmdline, MAX_LINE_LEN, stdin);
	if(fgets_handle == NULL){
	  printf("\n");
	        kill(0, SIGKILL);
	}
	

	/*Returns if user has entered no command or just a space*/
	if(strcmp(cmdline, "") == 10 || strcmp(cmdline, "") == 32)
	        return 0;

//	fgets(cmdline, MAX_LINE_LEN, stdin);
	
	/*Returns if user has entered no command or just a space*/
	if(strcmp(cmdline, "") == 10 || strcmp(cmdline, "") == 32)
	  return 0;

	parse_command(cmdline, &command);

	if(builtin_cmd(&command) == 0) { //try to execute built in command
		free_command(&command);
		return 0;
	}

	if(cmd_exists(&command) != 0) {//check if valid command including in path
		free_command(&command);
		return -1;
	}

	/* Create a child process to execute the command. */
	pid = fork();
	if (pid == 0) {
		/* The child executes the command. */
		execv(command.name, command.argv);
		fprintf(stderr, "launch: Error executing command '%s'\n",
					command.name);
			return EXIT_FAILURE;
	} else if (pid < 0) {
			fprintf(stderr, "launch: Error while forking\n");
		return EXIT_FAILURE;
	}

	pid = wait(&status);

	free_command(&command);

	return pid;
}

//parses a command from char* to command_t
int parse_command(char *cmdline, command_t *cmd)
{
    int argc = 0;
    char* word;
    
    /* Fill argv. */
    word = strtok(cmdline, WHITESPACE);
	if(word == NULL)
		return -1;

    while (word) {
        cmd->argv[argc] = (char *) malloc(strlen(word)+1);
		strcpy(cmd->argv[argc], word);
		word = strtok(NULL, WHITESPACE);
		argc++;
    }
    cmd->argv[argc] = NULL;

    /* Set argc and the command name. */
    cmd->argc = argc;
    cmd->name = (char *) malloc(strlen(cmd->argv[0])+1);
    strcpy(cmd->name, cmd->argv[0]);

    return 0;
}

//changes the current directory
int cd(char *pth){
	int status = 0;
	char path[BUFFERSIZE];
	strcpy(path,pth);

	if(path[0] == '-') {
		char* currPath = malloc(BUFFERSIZE);
		getcwd(currPath, BUFFERSIZE);
		chdir(lastPath);
		lastPath = currPath;
		return 0;
	}

   char cwd[BUFFERSIZE]; 
   if(pth[0] != '/')
   {
    getcwd(cwd,sizeof(cwd));
    strcat(cwd,"/"); 
    strcat(cwd,path);
    status = chdir(cwd);
   }else{
    status = chdir(pth);
   }

   if(status != 0)
	   fprintf(stderr, "cd: Error changing directory\r\n");

   return status;
}

//prints help
void help(){
    printf("Welcome to our simple shell program! \r\n Available commands in this shell are: \r\n cd - change directory \r\n exit - exit the shell");
    printf("   \r\n help - list the commands \r\n dragon -  Creative little function for the shell intended for the extra credit portion \r\n");
}


//prints dragon ascii
void dragon(){
	printf("                                                 __----~~~~~~~~~~~------___ \r\n");
	printf("                                      .  .   ~~//====......          __--~ ~~ \r\n");
	printf("                      -.            \\_|//     |||\\\\  ~~~~~~::::... /~ \r\n");
	printf("                   ___-==_       _-~o~  \\/    |||  \\\\            _/~~- \r\n");
	printf("           __---~~~.==~||\\=_    -_--~/_-~|-   |\\\\   \\\\        _/~ \r\n");
	printf("       _-~~     .=~    |  \\\\-_    '-~7  /-   /  ||    \\      / \r\n");
	printf("     .~       .~       |   \\\\ -_    /  /-   /   ||      \\   / \r\n");
	printf("    /  ____  /         |     \\\\ ~-_/  /|- _/   .||       \\ / \r\n");
	printf("    |~~    ~~|--~~~~--_ \\     ~==-/   | \\~--===~~        .\\ \r\n");
	printf("             '         ~-|      /|    |-~\\~~       __--~~ \r\n");
	printf("                         |-~~-_/ |    |   ~\\_   _-~            /\\ \r\n");
	printf("                              /  \\     \\__   \\/~                \\__ \r\n");
	printf("                          _--~ _/ | .-~~____--~-/                  ~~==. \r\n");
	printf("                         ((->/~   '.|||' -_|    ~~-/ ,              . _|| \r\n");
	printf("                                    -_     ~\\      ~~---l__i__i__i--~~_/ \r\n");
	printf("                                    _-~-__   ~)  \\--______________--~~ \r\n");
	printf("                                  //.-~~~-~_--~- |-------~~~~~~~~ \r\n");
	printf("                                         //.-~~~--\\  \r\n");
}
