package interpreter.commands;

import interpreter.commands.Command;
import interpreter.core.Environment;
import interpreter.core.Token;
import interpreter.result.Result;
import interpreter.result.Error;

import java.util.ArrayList;
import java.util.Iterator;

import driver.JShell;
import filesystem.Directory;
import filesystem.FSResultType;
import filesystem.File;
import filesystem.FileSystem;
import filesystem.FileSystemResolveResult;
import filesystem.FileSystemResult;
import filesystem.Node;
import filesystem.Path;
import filesystem.PathException;

/**
 * Implements the CP command with the semantics as noted in the docstring
 * 
 * @author mwb
 *
 */
public class CommandCp extends Command {

	/* The execute() for CommandCp implements cp, copy file -> file,
	 * copy file->dir/, or copy dir->dir (recursive).
	 * Check the command-line arguments and dispatch the interpreter.commands.
	 * 
	 *  @see interpreter.commands.Command#execute(driver.JShell, 
     *        interpreter.core.Environment, java.util.ArrayList)
	 */
	@Override
	public Result execute(JShell shell, Environment env, ArrayList<Token>
	arguments) {
	  if (arguments.size() != 2) {
		  env.err.println("cp takes two arguments, SRC and DEST");
		  return new Error();
	  }
	  
	  // grab arguments
	  String srcPathString=arguments.get(0).getBody();
	  String destPath=arguments.get(1).getBody();
	  FileSystem fs=shell.getFileSystem();
	  
	  // ensure source exists; give error user if not.
	  FileSystemResolveResult resolveResult;  
	  resolveResult=fs.resolvePath(srcPathString);
	  if (resolveResult.status!=FSResultType.Success) {
		  env.err.println(resolveResult.status.toString());
		  return new Error();
	  }
	  Node srcNode = resolveResult.node;

	  
	  // based on type of source, dispatch to correct copy helper
      String srcName = FileSystem.baseName(srcPathString);
	  if (srcNode.isFile()) {
	    return fileCopy(fs, env, (File) srcNode,srcName,destPath);
	  } else if (srcNode.isDirectory()) {
	    return dirCopy(fs, env, ((Directory) srcNode),srcName,destPath);
	  } else {
		env.err.print(srcPathString+": unrecognized type\n");  
		return new Error();
	  }
	}
	
	/**
	 * dirCopy recursive copies the contents of one directory to another.
	 * If the destination does not exist, attempts to create a new
	 * directory with that name and copy the contents of the source. 
	 * If the destination does exist, create the new directory (with name
	 * srcName) within it, and copy the contents there.
	 * 
	 * @param fs The filesystem in which to do the work.
	 * @param srcDir the source directory node in the filesystem.
	 * @param srcName the basename of the source directory.
	 * @param destPath the path to the destination
	 * @return
	 */
	private Result dirCopy(FileSystem fs, Environment env, Directory srcDir,
			String srcName, String destPath) {
	  
	  Node destNode = null;
	  FileSystemResolveResult resolveResult = fs.resolvePath(destPath);
	  if( resolveResult.status == FSResultType.Success) {
	    if (resolveResult.node.isDirectory() &&
	        fs.isSubPathOrEqual(srcDir,(Directory)(resolveResult.node))) {
	      // we're about to make a loop; error!
	    	env.err.println("Illegal operation");
	      return new Error();
	    }
	    // exact dest path exists, so we need to create a dir inside it
	    destPath = destPath+"/"+srcName;
	    FileSystemResult result=fs.mkdir(destPath);
	    if (result.status!=FSResultType.Success) {
	    	env.err.println(result.status.toString());
	      return new Error();
	    }
	    destNode=fs.resolvePath(destPath).node;
	  } else if (resolveResult.status==FSResultType.NotFound) {
	    // the exact path doesn't exist, but if the parent (dirname)
	    // of the path does, we're creating a new directory
	    String destPathDir = FileSystem.dirName(destPath);
	    String destPathBase = FileSystem.baseName(destPath);
	    resolveResult = fs.resolvePath(destPathDir);
	    if(resolveResult.status!=FSResultType.Success) {
	    	env.err.print(destPathDir+": "+resolveResult.status.toString());
	      	return new Error();
	    } else {
	      if( resolveResult.node.isDirectory() &&
	          fs.isSubPathOrEqual(srcDir,(Directory)(resolveResult.node))) {
	        // we're about to make a loop, error!
	    	  env.err.println("Illegal operation");
	    	  return new Error();
	      }
	      destPath = destPathDir + "/" + destPathBase;
	      FileSystemResult result=fs.mkdir(destPath);
	      if(result.status!=FSResultType.Success) {
	    	  env.err.println(result.status.toString());
	    	  return new Error();
	      }
	      destNode=fs.resolvePath(destPath).node;
	    }
	  } else {
		  env.err.println(destPath+": "+resolveResult.status.toString());
	      return new Error();
	  }
	  assert(destNode.isDirectory());
	  
	  // now take all the entries from inside the src dir and copy them
	  Iterator<String> dirIter=srcDir.getEntries().keySet().iterator();
	  while(dirIter.hasNext()) {
	    String name=dirIter.next();
	    Node node=srcDir.getNodeByName(name);
	    if (node.isFile()) {
	      Result userResult=fileCopy
	          (fs, env, ((File)node),name,destPath+"/"+name);
	      if (userResult.isError()) {
	        return userResult;
	      }
	    } else if (node.isDirectory()) {
	      Result userResult=dirCopy
	          (fs,env, (Directory)node,name,destPath+"/"+name);
	      if (userResult.isError()) {
	        return userResult;
	      }
	    }
	  }
	  return Command.okayResult;	  
	}

	/**
	 * A simple copy of a file from one location to another. If another
	 * file exists at the destination, it's overwritten (and retains its
	 * old name) If the file does not exist at the named destination, we
	 * attempt to create it with the name srcName.
	 * Then the contents of the srcNode are written into the destination.
	 * 
	 * @param fs the filesystem in which we're operating.
	 * @param srcNode the node from which we draw our content.
	 * @param srcName the baseName of the old file.
	 * @param destPath the place
	 * @return
	 */
	private Result fileCopy
	(FileSystem fs, Environment env, File srcFile
	        ,String srcName,String destPath) {
	  String content = srcFile.getContent();	  
	  Path destPathPath=new Path(fs,destPath);	  
	  try {
	    File destFile;
	    if (!destPathPath.exists()) {
	      destFile=destPathPath.getOrCreateFile();
	    } else {
	      if (destPathPath.isDirectory()) {
	        destFile = destPathPath.withSuffix(srcName).getOrCreateFile();
	      } else {
	        destFile = destPathPath.getOrCreateFile();
	        if (destFile==srcFile) {
	        	env.err.print("cp: "+srcName+" and "+destPathPath.toString()+
	              " are identical (not copied)");
	          return new Error();
	        }
	      }
	    }
	    destFile.setContent(content);
	  } catch (PathException e) {
		  env.err.println(e.toString());
	    return new Error();
	  }
	  return Command.okayResult;    
	}
	
	/* (non-Javadoc)
	 * @see interpreter.commands.Command#getName()
	 */
	@Override
	public String getName() {
		return "cp";
	}

	/* (non-Javadoc)
	 * @see interpreter.commands.Command#getDocString()
	 */
	@Override
	public String getDocString() {
		return "NAME: cp -- copy files"+ "\n"
        +"SYNOPSIS: cp oldpath newpath" + "\n"
        +"\n"
        +"DESCRIPTION: Copy item oldpath to newpath If OLDPATH is a"+"\n"
        +"directory recursively copy the contents";
	}

}