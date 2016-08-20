import java.util.*;
import java.io.*;

/*
@author(MD Danish Ansari)
@version(1.0.1)
@lastupdated(11 Aug 2016)
 */

public class Lumax
{
    private static Scanner appFilter,sc;
    private static File inputFile;
    private static File[] icons;
    private static ArrayList<String> appName,packageName,activityName,drawableName,finalDrawable;
    private static ArrayList<Integer> userIndex;
    private static String icons_dir, xml_dir,parents_dir;
    private static int DASHBOARD_TYPE;
    //1 for iconshowcase
    //2 for material dashboard
    //3 for polar dashboard

    public static void main(String args[])
    {

        //directories
        parents_dir=System.getProperty("user.dir");
        parents_dir=parents_dir.replace("%20"," ");
        icons_dir=parents_dir+"\\icons\\";
        xml_dir=parents_dir+"\\xml\\";

        appName=new ArrayList<String>();
        packageName=new ArrayList<String>();
        activityName=new ArrayList<String>();
        drawableName=new ArrayList<String>();

        try{
            inputFile=new File("appfilter.xml");
            appFilter=new Scanner(inputFile);
            while(appFilter.hasNextLine())
            {
                String cLine=appFilter.nextLine();
                if (cLine.contains("<item"))
                {
                    if(cLine.contains("<item component=") && cLine.contains("\"/>"))
                    {
                        DASHBOARD_TYPE=1;
                    }
                    else if(cLine.contains("<item component=") && cLine.contains("\" />"))
                    {
                        DASHBOARD_TYPE=2;
                    }
                    else
                    {
                        DASHBOARD_TYPE=3;
                    }
                }
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("\n  appfilter.xml file not found.");
        }

        icons=Scan4Images();
        if(icons==null)
        {
            System.out.println("\n  \"icons\" folder not found.");
            System.exit(1);	
        }
        if(icons.length<=0)
        {
            System.out.println("\n  No icons found in \"icons\" folder.");
            System.exit(1);	
        }

        switch(DASHBOARD_TYPE)
        {
            case 1:
            Iconshowcase i =new Iconshowcase();
            i.fillValues();
            fillFinalDrawable();
            takeInput();
            finalizeXML();
            break;
            case 2:
            MaterialDashboard md =new MaterialDashboard();
            md.fillValues();
            fillFinalDrawable();
            takeInput();
            finalizeXML();
            break;
            case 3:
            Polar  p =new Polar();
            p.fillValues();
            fillFinalDrawable();
            takeInput();
            finalizeXML();
            break;
            default:
            break;
        }

    }

    private static File[] Scan4Images()
    {
        File directory = new File(icons_dir);

        return directory.listFiles(new FilenameFilter()
            { 
                public boolean accept(File dir, String filename)
                {
                    return filename.endsWith(".png") || filename.endsWith(".PNG");
                }
            } );
    }

    private static void fillFinalDrawable()
    {
        finalDrawable=new ArrayList<String>();

        for(File img:icons)
        {
            finalDrawable.add(img.getName().replace(".png", "").replace(".PNG", "").trim());
        }
        System.out.println();
        for(int i=0;i<finalDrawable.size();i++)
        {
            System.out.println("  ["+i+"]    "+finalDrawable.get(i));
        }
    }

    public static void takeInput()
    {
        sc=new Scanner(System.in);
        userIndex=new ArrayList<Integer>();
        System.out.println("\n  Enter the corresponding number for the apps. Type -1 to skip particular app.");
        for(int i=0;i<appName.size();i++)
        {
            System.out.print("\n  "+appName.get(i)+": ");
            userIndex.add(sc.nextInt());
        }
    }

    public static void finalizeXML()
    {
        try
        {
            new File(xml_dir).mkdir();
            PrintWriter appfilterBuilder=new PrintWriter(new File(xml_dir+"appfilter.xml"));
            PrintWriter themeresoucesBuilder=new PrintWriter(new File(xml_dir+"theme_resources.xml"));
            PrintWriter appmapBuilder=new PrintWriter(new File(xml_dir+"appmap.xml"));
            PrintWriter drawableBuilder=new PrintWriter(new File(xml_dir+"drawable.xml"));
            PrintWriter iconpackBuilder=new PrintWriter(new File(xml_dir+"icon_pack.xml"));
            for(int i=0;i<appName.size();i++)
            {
                if(userIndex.get(i)==-1)
                {
                    continue;
                }
                appfilterBuilder.println("<!-- "+appName.get(i)+" -->\n<item component=\"ComponentInfo{"+packageName.get(i)+"/"+activityName.get(i)+"}\" drawable=\""+finalDrawable.get(userIndex.get(i))+"\"/>\n");
                themeresoucesBuilder.println("<!-- "+appName.get(i)+" -->\n<AppIcon name=\""+packageName.get(i)+"/"+activityName.get(i)+"\" image=\""+finalDrawable.get(userIndex.get(i))+"\"/>\n");
                appmapBuilder.println("<!-- "+appName.get(i)+" -->\n<item class=\""+activityName.get(i)+"\" name=\""+finalDrawable.get(userIndex.get(i))+"\"/>\n");
                drawableBuilder.println("<!-- "+appName.get(i)+" -->\n<item drawable=\""+finalDrawable.get(userIndex.get(i))+"\"/>\n");
                iconpackBuilder.println("<!-- "+appName.get(i)+" -->\n<item>"+finalDrawable.get(userIndex.get(i))+"</item>\n");
            }

            appfilterBuilder.close();
            themeresoucesBuilder.close();
            appmapBuilder.close();
            drawableBuilder.close();
            iconpackBuilder.close();
            System.out.println("\n  All XML files created successfully. Check out the generated\"xml\" folder.");
        }
        catch(FileNotFoundException e)
        {
            System.out.println(e);
        }
    }
    public static class MaterialDashboard
    {
        public static void fillValues()
        {
            try
            {

                appFilter=new Scanner(inputFile);

                while(appFilter.hasNextLine())
                {
                    String cLine=appFilter.nextLine();
                    if(cLine.contains("<!--"))
                    {
                        cLine=cLine.replace("<!--","").replace("-->","").trim();
                        appName.add(cLine);
                    }
                    else if(cLine.contains("<item component="))
                    {
                        String pckge=cLine.substring(31,cLine.indexOf("/"));
                        packageName.add(pckge);

                        String activity=cLine.substring(cLine.indexOf("/")+1,cLine.indexOf("}"));
                        activityName.add(activity);

                        String drawable=cLine.substring(cLine.indexOf("drawable=\"")+10,cLine.indexOf("\" />"));
                        drawableName.add(drawable);
                    }
                }

            }
            catch(FileNotFoundException e)
            {
                System.out.println(e);
            }
        }
    }
    public static class Polar
    {
        public static void fillValues()
        {
            try
            {

                appFilter=new Scanner(inputFile);

                while(appFilter.hasNextLine())
                {
                    String cLine=appFilter.nextLine();
                    if(cLine.contains("<!--"))
                    {
                        cLine=cLine.replace("<!--","").replace("-->","").trim();
                        appName.add(cLine);
                    }
                    else if(cLine.contains("component="))
                    {
                        String pckge=cLine.substring(cLine.indexOf("fo{")+3,cLine.indexOf("/"));
                        packageName.add(pckge);

                        String activity=cLine.substring(cLine.indexOf("/")+1,cLine.indexOf("}"));
                        activityName.add(activity);
                    }
                    else if(cLine.contains("drawable="))
                    {
                        String drawable=cLine.substring(cLine.indexOf("drawable=\"")+10,cLine.indexOf("\" />"));
                        drawableName.add(drawable);
                    }
                }

            }
            catch(FileNotFoundException e)
            {
                System.out.println(e);
            }
        }
    }
    public static class Iconshowcase
    {
        public static void fillValues()
        {
            try
            {

                appFilter=new Scanner(inputFile);

                while(appFilter.hasNextLine())
                {
                    String cLine=appFilter.nextLine();
                    if(cLine.contains("<!--"))
                    {
                        cLine=cLine.replace("<!--","").replace("-->","").trim();
                        appName.add(cLine);
                    }
                    else if(cLine.contains("<item component="))
                    {
                        String pckge=cLine.substring(31,cLine.indexOf("/"));
                        packageName.add(pckge);

                        String activity=cLine.substring(cLine.indexOf("/")+1,cLine.indexOf("}"));
                        activityName.add(activity);

                        String drawable=cLine.substring(cLine.indexOf("drawable=\"")+10,cLine.indexOf("\"/>"));
                        drawableName.add(drawable);
                    }
                }

            }
            catch(FileNotFoundException e)
            {
                System.out.println(e);
            }
        }

    }
}