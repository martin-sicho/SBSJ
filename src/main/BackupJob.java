package main;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.*;

/**
 * This class holds everything the BackupManager class
 * needs to perform action.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
class BackupJob {
    private Path mDirInput;
    private Path mDirOutput;
    private boolean mListOnly;
    private boolean mList;
    private boolean mShallow;

    BackupJob(Namespace args) {
        if (args != null) {
            System.out.println(args);
            // mozna bych mohl predelat pomoci  Arrays.asList(...).contains(...)
            if (args.get("input") == null
                    && args.get("output") == null
                    && args.getBoolean("list_backups")
                    ) {
                //System.out.println("jenom vypisu zalohy");
                mListOnly = true;
                return;
            }
            else if (args.get("input") == null
                    && args.get("output") != null
                    ||
                    args.get("input") != null
                    && args.get("output") == null
                    ) {
                    System.out.println("You have to specify both SOURCE and DESTINATION!");
                    System.exit(-1);
            }
            else  if (args.get("input") != null
                    && args.get("output") != null
                    && args.getBoolean("list_backups")
                    ) {
                //System.out.println("vytvorim novou zalohu a pak vypisu");
                mList = true;
                parseLocations(args);
            }
            else  if (args.get("input") != null
                    && args.get("output") != null
                    ) {
                //System.out.println("vytvorim jen novou zalohu");
                mList = false;
                parseLocations(args);
            }
            else {
                System.out.println("Unknown combination of arguments:");
                System.out.println(args.getAttrs().keySet());
                System.exit(-1);
            }

            //other arguments
            mShallow = args.getBoolean("shallow");

        } else {
            System.exit(0);
        }
    }

    private void parseLocations(Namespace args) {
        try {
            mDirInput = Paths.get(args.getString("input")).toRealPath();
        } catch (IOException exp) {
            System.out.println("The specified SOURCE path: " + args.getString("input") + " doesn't exist");
            System.exit(-1);
        }
        mDirOutput = Paths.get(args.getString("input"));
    }
}
