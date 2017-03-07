/*     */ package com.maximuspayne.navycraft.config;
/*     */ 
/*     */ import com.maximuspayne.navycraft.NavyCraft;
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import org.w3c.dom.Comment;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class XMLHandler
/*     */ {
/*     */   public static void load()
/*     */   {
/*  27 */     File dir = NavyCraft.instance.getDataFolder();
/*  28 */     if (!dir.exists()) {
/*  29 */       dir.mkdir();
/*     */     }
/*  31 */     File config = new File(NavyCraft.instance.getDataFolder(), NavyCraft.instance.configFile.filename);
/*  32 */     if (!config.exists()) {
/*  33 */       return;
/*     */     }
/*  35 */     Document doc = null;
/*     */     try {
/*  37 */       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
/*  38 */       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
/*  39 */       doc = dBuilder.parse(config.toURI().getPath());
/*  40 */       doc.getDocumentElement().normalize();
/*     */       
/*     */       Object[] arrayOfObject;
/*     */       
/*  44 */       int j = (arrayOfObject = NavyCraft.instance.configFile.ConfigSettings.keySet().toArray()).length; for (int i = 0; i < j; i++) { Object configLine = arrayOfObject[i];
/*  45 */         String configKey = (String)configLine;
/*     */         
/*  47 */         NodeList list = doc.getElementsByTagName(configKey);
/*     */         try
/*     */         {
/*  50 */           String value = list.item(0).getChildNodes().item(0).getNodeValue();
/*  51 */           NavyCraft.instance.configFile.ConfigSettings.put(configKey, value);
/*     */         }
/*     */         catch (Exception localException1) {}
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  58 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void save() {
/*  63 */     File dir = NavyCraft.instance.getDataFolder();
/*  64 */     if (!dir.exists()) {
/*  65 */       dir.mkdir();
/*     */     }
/*  67 */     File configuration = new File(NavyCraft.instance.getDataFolder(), NavyCraft.instance.configFile.filename);
/*     */     
/*     */ 
/*  70 */     Element setting = null;
/*     */     try
/*     */     {
/*  73 */       DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
/*     */       
/*  75 */       DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
/*  76 */       Document doc = docBuilder.newDocument();
/*  77 */       doc.setXmlStandalone(true);
/*  78 */       Element rootElement = doc.createElement("NavyCraft-Configuration");
/*  79 */       doc.appendChild(rootElement);
/*     */       
/*  81 */       Object[] obj = NavyCraft.instance.configFile.ConfigSettings.keySet().toArray();
/*  82 */       Arrays.sort(obj);
/*  83 */       Object[] arrayOfObject1; int j = (arrayOfObject1 = obj).length; for (int i = 0; i < j; i++) { Object configLine = arrayOfObject1[i];
/*  84 */         String configKey = (String)configLine;
/*  85 */         setting = doc.createElement(configKey);
/*  86 */         setting.appendChild(doc.createTextNode((String)NavyCraft.instance.configFile.ConfigSettings.get(configKey)));
/*  87 */         rootElement.appendChild(setting);
/*     */         
/*  89 */         if (NavyCraft.instance.configFile.ConfigComments.containsKey(configKey)) {
/*  90 */           Comment comment = doc.createComment((String)NavyCraft.instance.configFile.ConfigComments.get(configKey));
/*  91 */           rootElement.insertBefore(comment, setting);
/*     */         }
/*     */       }
/*     */       
/*  95 */       Comment comment = doc.createComment("Do you find this config file strange and confusing? This guy made this nifty GUI editor for it. Get it here: http://bit.ly/ewPebA");
/*     */       
/*  97 */       rootElement.appendChild(comment);
/*     */       
/*  99 */       TransformerFactory transformerFactory = TransformerFactory.newInstance();
/*     */       try
/*     */       {
/* 102 */         Transformer transformer = transformerFactory.newTransformer();
/* 103 */         transformer.setOutputProperty("indent", "yes");
/* 104 */         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
/* 105 */         DOMSource source = new DOMSource(doc);
/* 106 */         StreamResult result = new StreamResult(configuration);
/* 107 */         transformer.transform(source, result);
/*     */       } catch (TransformerConfigurationException e) {
/* 109 */         e.printStackTrace();
/*     */       } catch (TransformerException e) {
/* 111 */         e.printStackTrace();
/*     */       }
/*     */       return;
/* 114 */     } catch (ParserConfigurationException e1) { e1.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\config\XMLHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */