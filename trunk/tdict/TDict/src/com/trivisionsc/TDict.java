package com.trivisionsc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.translate.Translate;
import com.google.tts.TTS;

public class TDict extends Activity  {
	static final private int MENU_ITEM = Menu.FIRST;
	
	//for check
	private boolean isReconvert=false;
	private boolean isConvert;
	private boolean isSuccess;
	int test=0; 
	//for control
	private ImageButton btnClear = null;
	private ImageButton btnSpeak = null;
	ImageButton btnInfor = null;
	private ListView listView=null;
	private EditText input=null;
	private TextView textContent=null;
	private TextView view=null;
	private LinearLayout layout=null;
	private RadioGroup radiogroup=null;
	private Spinner spnSourceLanguages;
    private Spinner spnDestinationLanguages;
    private Button btnConvert;
    private Button btnReConvert;
    private Button btnGoogleClear;
    private ImageButton btnAudioConvert;
    private ImageButton btnAudioReConvert;
    private EditText edtSourceInput;
    private EditText edtDestinationInput;
    private static ProgressDialog dialog;
    Menu menu=null;	
	// for adapter
	SeparatedListAdapter adapter=null;
	ArrayAdapter<String> aspnLanuages;
	// for database
	private SQLiteDatabase nameDb;
	static String selectedDb=null;
	// for store data
	ArrayList<String> listWordCurrent=null;
	ArrayList<String> listContentCurrent=null;
	ArrayList<String> listFileDb=null;
	//for text to speech
	private TTS myTts;
	private TTS myTtsForGoogle=null;
	private String translatedText=null;
    //for store language for Translate
    private static String[] mLanguageNames =null; 
    private static String[] mLanguagesValues =null;
    private List<String> allLanguageNames;
    private List<String> allLanguagesValues;
    //for index
    private int sourceIndex;
    private int destinationIndex;
    //for thread
    private Thread searchAdress;
    // for display font VN
    private Typeface face;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) 
    {
      	super.onCreate(icicle);
       	 // setting for Dictionary
      	 face=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf"); 
         loadForDictinary(true);
       	
    }
    
   // create Menu for Program
   @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  super.onCreateOptionsMenu(menu);

	  // Group ID
	  int groupId = 0;
	  // Unique menu item identifier. Used for event handling.
	  int menuItemId = MENU_ITEM;
	  // The order position of the item
	  int menuItemOrder = Menu.NONE;
	  this.menu=menu;
	  // Added extra items to make sure there's more than six to 
	  // force the extended menu to appear.
	  menu.add(groupId, menuItemId+0, menuItemOrder, "Dictionary").setIcon(R.drawable.dictmenu);
	  menu.add(groupId, menuItemId+2, menuItemOrder, "Google Translate").setIcon(R.drawable.google);
	  menu.add(groupId, menuItemId+1, menuItemOrder, "Manage Dictionary").setIcon(R.drawable.manage);
	  menu.add(groupId, menuItemId+3, menuItemOrder, "About").setIcon(R.drawable.info);
	 
	  return true;
	}

   // process event select Menu
	public boolean onOptionsItemSelected(MenuItem item) {
		  super.onOptionsItemSelected(item);
				
		  // Find which menu item has been selected
		  switch (item.getItemId()) {
			  case (MENU_ITEM+0): 
		      {	 
		    	// setting for Dictionary
	         	loadForDictinary(false);
		    	break;
		      }
		      case (MENU_ITEM+1): 
		      {
		    	  // setting for MangeDictinary
		    	  loadForManageDictionary();
		    	  break;
		      }
		      case (MENU_ITEM+2): 
		      {
		    	  // load for GoogleTranslate	
			  	  loadForGoogleTranslate();
			  	  break;
		      }
		
		      case (MENU_ITEM+3): 
		      {	 
		    	  // load for About
		    	  loadForAbout();
		    	  break;
		      }
		  }
		  
		   return true;
		}
		
	// create menu context
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  	menu.setHeaderTitle("Select a dictionary");  
		ArrayList<String> listFileDb=getDataFileArray();
		  for (int i=0;i<listFileDb.size();i++)
		  {	  
			  menu.add(0, 0, i, listFileDb.get(i));
			 
		  }

	}
	
	// process for event select item of Manage Dictionary 
	@Override  
      public boolean onContextItemSelected(MenuItem aItem) {  
           input.setText(aItem.getTitle());
           return true; /* true means: "we handled the event". */  
        
    }
	
	// handler use for Progress bar of GoogleTranslate
	private Handler showProgressGoogleTranslate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	if(!isReconvert)
        	{
	        	if(test==0)
	        	{
	        		edtDestinationInput.setText(translatedText);
	        	}
	        	else
	        	{
					 LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				     View viewError = inflate.inflate(R.layout.message_err,null);
					 AlertDialog.Builder dialog=new AlertDialog.Builder(TDict.this).setIcon(
				  				R.drawable.warning).setTitle("Unable to connect")
				  				.setView(viewError).setPositiveButton("OK",
				  						new DialogInterface.OnClickListener() {
				  							public void onClick(DialogInterface dialog,
				  									int whichButton) {
				  								;								  								
				  							}
				  						});
					 dialog.show();
				
	        	}        		
        	}
        	else
        	{
        		if(test==0)
	        	{
        			String input=edtDestinationInput.getText().toString();
        			edtDestinationInput.setText(translatedText);
					edtSourceInput.setText(input);
					btnAudioConvert.setEnabled(true);
					btnAudioReConvert.setEnabled(true);
	        	}
        		else
        		{
        			 LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				     View viewError = inflate.inflate(R.layout.message_err,null);
					 AlertDialog.Builder dialog=new AlertDialog.Builder(TDict.this).setIcon(
				  				R.drawable.warning).setTitle("Unable to connect")
				  				.setView(viewError).setPositiveButton("OK",
				  						new DialogInterface.OnClickListener() {
				  							public void onClick(DialogInterface dialog,
				  									int whichButton) {
				  								;								  								
				  							}
				  						});
					 dialog.show();
        			
        		}
        	}
			isSuccess=true;
			sourceIndex=spnSourceLanguages.getSelectedItemPosition();
  			destinationIndex=spnDestinationLanguages.getSelectedItemPosition();
			if (allLanguagesValues.get(destinationIndex).equals("en")) 
			{
				btnAudioReConvert.setEnabled(true);
			}
			else
			{
				btnAudioReConvert.setEnabled(false);
			}
			if (allLanguagesValues.get(sourceIndex).equals("en")) 
			{
				btnAudioConvert.setEnabled(true);
			}
			else
			{
				btnAudioConvert.setEnabled(false);
			}
	
        	
        	dialog.dismiss();
        	
        		 
        }
    };	
	// binding data for ListView
	public void setDataForListView(String nameDictinary)
	{
		
		String stringQuery="SELECT Content,Word FROM "+nameDictinary+" LIMIT 15 OFFSET 0";
	   	listView.setVisibility(View.VISIBLE);
	 	textContent.setVisibility(View.INVISIBLE);
	   	Cursor result = nameDb.rawQuery(stringQuery,null);
	   	int indexWordColumn = result.getColumnIndex("Word");
        int indexContentColumn = result.getColumnIndex("Content");
        if (result != null)
        {
        	int countRow=result.getCount();
        	if (countRow>=1)
        	{
        		listContentCurrent = new ArrayList<String>();
    	    	listWordCurrent = new ArrayList<String>();
        	    result.moveToFirst();
        		String word = Utility.decodeContent(result.getString(indexWordColumn));
        		String content = Utility.decodeContent(result.getString(indexContentColumn));
                listWordCurrent.add(0,word);
                listContentCurrent.add(0,content);
                int i = 0;
                    while (result.moveToNext()) 
                    {
                    	word = Utility.decodeContent(result.getString(indexWordColumn));
                    	content = Utility.decodeContent(result.getString(indexContentColumn));
                        listWordCurrent.add(i,word);
                        listContentCurrent.add(i,content);
                        i++;
                    } 
                
                ArrayAdapter<String> tempAdapter=new ArrayAdapter<String>(getApplicationContext(),	R.layout.todolist_item,listWordCurrent);
                tempAdapter.setDropDownViewResource(R.layout.todolist_item);
                adapter.addSection("",tempAdapter);
                listView.setAdapter(adapter);
            }
           
       }
      	   
	 }
	// binding data for change Word
	public void setDataForListViewChangeWord(String nameDictinary,String valueInput)
	{
		// encode input  
		String valueEncode=Utility.encodeContent(valueInput);
		// query in database	
		String stringQuery=null;
		if (valueInput!=null&&!valueInput.equals(""))
		{
			
		    stringQuery="SELECT Content,Word FROM "+nameDictinary+" WHERE  word>='"+valueEncode+"' and word<='"+valueEncode+"zzzz' LIMIT 15 OFFSET 0";
	 	  
		}
		else
		{
			stringQuery="SELECT Content,Word FROM "+nameDictinary+" LIMIT 15 OFFSET 0" ;
		}
		//assign result return
	   	Cursor result = nameDb.rawQuery(stringQuery,null);
	   	int indexWordColumn = result.getColumnIndex("Word");
        int indexContentColumn = result.getColumnIndex("Content");
        if (result != null)
        {
        	int countRow=result.getCount();
        	if (countRow>=1)
        	{
        		listContentCurrent = new ArrayList<String>();
    	    	listWordCurrent = new ArrayList<String>();
        	    result.moveToFirst();
        		String word = Utility.decodeContent(result.getString(indexWordColumn));
        		String content = Utility.decodeContent(result.getString(indexContentColumn));
        	    listWordCurrent.add(0,word);
                listContentCurrent.add(0,content);
                int i = 0;
                    while (result.moveToNext()) 
                    {
                    	word = Utility.decodeContent(result.getString(indexWordColumn));
                    	content = Utility.decodeContent(result.getString(indexContentColumn));
                        listWordCurrent.add(i,word);
                        listContentCurrent.add(i,content);
                        i++;
                    } 
               
            }
        	ArrayAdapter<String> tempAdapter=new ArrayAdapter<String>(getApplicationContext(),	R.layout.todolist_item,listWordCurrent);
            tempAdapter.setDropDownViewResource(R.layout.todolist_item);
            adapter.addSection("",tempAdapter);
            listView.setAdapter(tempAdapter);
            
       }
		
      	   
	 }
	// Method to get Array File Database
	public ArrayList<String> getDataFileArray()
	{
		ArrayList<String> list=new ArrayList<String>();
		String pathFileData=getResources().getString(R.string.path_file_data);
		String formatFileData=getResources().getString(R.string.format_file_data);
		
		File dataDirectory = new File(pathFileData);
        File[] listFile=dataDirectory.listFiles();
        for (File currentFile : listFile)
        {
        if (currentFile.isFile()&&currentFile.getName().contains(formatFileData))
        {
        	String nameFullFile=currentFile.getName();
          	int index=nameFullFile.indexOf(formatFileData);
          	String nameFile=nameFullFile.substring(0, index);
          	list.add(nameFile);
        }
        }
        
        return list;			
	}
	
	 //process speak only for GoogleTranslate 
	 public void processSpeakForGoogle()
	  {
		myTtsForGoogle = new TTS(this, ttsInitListenerForGoole, true);
	  }
	 //create TTS for GoogleTranslte
	 private TTS.InitListener ttsInitListenerForGoole = new TTS.InitListener() 
	 {
		 public void onInit(int version) 
		 {
			 try
	         {
				 if(isConvert)
	        	 {  
					 myTtsForGoogle.setLanguage(allLanguageNames.get(sourceIndex));
	        		 myTtsForGoogle.speak(edtSourceInput.getText().toString(),0, null);
	        	 }
	        	 else
	        	 {
	        		 myTtsForGoogle.setLanguage(allLanguageNames.get(destinationIndex));
	        		 myTtsForGoogle.speak(edtDestinationInput.getText().toString(),0, null);
	        	 }
	        	
	          }
	          catch(Exception ex)
	          {
	        	  ex.printStackTrace();  
	          }
	      }
	  };
	   
	//process speak only for Dictionary
    public void processSpeak()
    {
    	myTts = new TTS(this, ttsInitListener, true);
    }
		    
	//create TTS for Dictionary
	private TTS.InitListener ttsInitListener = new TTS.InitListener() 
	 {
		 public void onInit(int version) 
		 {
			 try
			 {
				 myTts.speak(input.getText().toString(),0, null);
			 }
			 catch(Exception ex)
			 {
			   	ex.printStackTrace();  
			 }
		 }
     };
	// process event click button clear 
	public void clearData() 
    {
    	input.setText("");
    	textContent.setText("");
    	textContent.setVisibility(View.INVISIBLE);
    	btnSpeak.setEnabled(false);
   		setDataForListView(selectedDb);
	
    }
	       
     // Load For Dictionary
	 public void loadForDictinary(boolean isInit)
	 {
		 	setContentView(R.layout.main);
		 	view = new TextView(this);
	       	registerForContextMenu(view);
	    	//initialize for control
	    	input =(EditText) findViewById(R.id.word);
	    	adapter = new SeparatedListAdapter(this);
	    	listView = (ListView)findViewById(R.id.listResult);
	    	textContent =(TextView) findViewById(R.id.textView);
	    	textContent.setVisibility(View.INVISIBLE);
	    	btnClear = (ImageButton) findViewById(R.id.btnClear);
	        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
	       	// Open Database
	       	ArrayList<String> listFileDb=getDataFileArray();
	       	if (listFileDb.size()>0)
	       	{
	   	       	String defalutDbName=listFileDb.get(1);
	   	       	if(isInit)
	   	       		selectedDb=defalutDbName;
		       	String pathFileData=getResources().getString(R.string.path_file_data);
		       	String fullPathFile=pathFileData+"/"+selectedDb+".db";
		       	nameDb=SQLiteDatabase.openDatabase(fullPathFile, null, SQLiteDatabase.OPEN_READONLY);
		        // Initialize for List View
		    	setDataForListView(selectedDb);
	       	}
	       	else
	       	{
	       		input.setEnabled(false);
	       		btnSpeak.setEnabled(false);
	       		btnClear.setEnabled(false);
	       		textContent.setVisibility(View.VISIBLE);
	       		String message=getResources().getString(R.string.no_database);
	       		textContent.setTextColor(Color.RED);
	       		textContent.setText(message);
	       		
	       	}
	      //PROCESS EVENTS
	    	//1....... process event enter word
	    	input.addTextChangedListener(new TextWatcher()
	    	{ 
	    		public void afterTextChanged(Editable s) 
	    		{
	    		   String valueInput=input.getText().toString();
	    		   // Search addresses
	    		   setDataForListViewChangeWord(selectedDb,valueInput);
	    					
	    		
	    		   listView.setVisibility(View.VISIBLE);
	    		   textContent.setVisibility(View.INVISIBLE);
	    		  // currentWord=input.getText().toString();
	    		} 
	    		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
	    		{;} 
	    	
	    	    public void onTextChanged(CharSequence s, int start, int before, int count) 
	    		{;}
	    	});

	    	//2........ process event select  item in ListView
	    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
	    			    String word=(String) adapter.getItem(arg2);
		    			textContent.setTypeface(face);  
		    			textContent.setVisibility(View.VISIBLE);
		    			if(arg2!=0)
		    				textContent.setText(listContentCurrent.get(arg2-1).toString());
		    			else
		    				{
		    					textContent.setText(listContentCurrent.get(arg2).toString());
		    					word=(String) adapter.getItem(arg2+1);
		    				}
		    			input.setText(word);
		    		    btnSpeak.setEnabled(true);
		    		    btnClear.setFocusable(false);
		    		 	listView.setVisibility(View.INVISIBLE);
	        }
	        });

	      	//3...........event click button clear data
	       
	        btnClear.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					clearData();
				}
			});
	 
	        //5..............event click button Speak
	        btnSpeak.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					processSpeak();
				}
			});
	        btnSpeak.setEnabled(false);
	    
	    	
	 }
	// Load For GoogleTranslate
	public void loadForGoogleTranslate()
	{
		  setContentView(R.layout.google_translate);
	  	  mLanguageNames =(String[]) getResources().getStringArray(R.array.listNameLanguage);
	      mLanguagesValues=(String[]) getResources().getStringArray(R.array.listValueLanguage);
	      allLanguageNames = new ArrayList<String>();
	      for (int i = 0; i < mLanguageNames.length; i++) {
	      	allLanguageNames.add(mLanguageNames[i]);
	      }
	      allLanguagesValues = new ArrayList<String>();
	      for (int i = 0; i < mLanguagesValues.length; i++) {
	      	allLanguagesValues.add(mLanguagesValues[i]);
	      }
	      // get control
	      btnConvert = (Button) findViewById(R.id.btnConvert);
	      btnReConvert = (Button) findViewById(R.id.btnReConvert);
	      btnGoogleClear=(Button) findViewById(R.id.btnGoogleClear);
	      
	      btnAudioConvert = (ImageButton) findViewById(R.id.btnAudioConvert);
	      btnAudioReConvert = (ImageButton) findViewById(R.id.btnAudioReConvert);
	          
	      edtSourceInput=(EditText) findViewById(R.id.edtSourceInput);
	      edtDestinationInput=(EditText) findViewById(R.id.edtDestinationInput);
	      edtSourceInput.setTypeface(face);  
	      edtDestinationInput.setTypeface(face);  
	      // list Language Source
	      spnSourceLanguages = (Spinner) findViewById(R.id.spnSourceLanguages);
	      spnDestinationLanguages = (Spinner) findViewById(R.id.spnDestinationLanguages);
	     
	      // list Language Destination
	      aspnLanuages = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, allLanguageNames);
	      aspnLanuages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      
	      // binding source for spinners
	      spnSourceLanguages.setAdapter(aspnLanuages);
	      spnDestinationLanguages.setAdapter(aspnLanuages);
	      //spnSourceLanguages.set

	      // when translate or swap success, Audio enable
	      btnAudioConvert.setEnabled(false);
	   	  btnAudioReConvert.setEnabled(false);
	      // event button
	      btnConvert.setOnClickListener(new View.OnClickListener()
	  		{
	  			public void onClick(View view)
	  			{
	  				isReconvert=false;
	  				final String input=edtSourceInput.getText().toString();
	  				sourceIndex=spnSourceLanguages.getSelectedItemPosition();
	  				destinationIndex=spnDestinationLanguages.getSelectedItemPosition();
	  				dialog = ProgressDialog.show(TDict.this,
	    					"Translating...",
	    					"Please wait a moment",true,false);
		    		
	    			searchAdress = new Thread() 
	    			{
	    				public void run()
	    				{
	    					
	    					try 
	    	  				{	
		    	  				translatedText = Translate.translate(input,allLanguagesValues.get(sourceIndex),allLanguagesValues.get(destinationIndex));
		    	  				test=0;
		    					showProgressGoogleTranslate.sendEmptyMessage(0);	
		    					
	    	  				}
	    					catch (Exception ex) 
	    	  				{
	    						test=1;
	    						showProgressGoogleTranslate.sendEmptyMessage(0);	
	    	  				}
	    				}
	    			};
		    		searchAdress.start();
	  				
	  			}
	  		});
	      //event clear screen
	      btnGoogleClear.setOnClickListener(new View.OnClickListener()
	      {
	  		public void onClick(View view)
	  		{
	  			spnSourceLanguages.setSelection(0);
	  			spnDestinationLanguages.setSelection(0);
	  			edtSourceInput.setText("");
	  			edtDestinationInput.setText("");
	  			btnAudioConvert.setEnabled(false);
				btnAudioReConvert.setEnabled(false);
	  		}
	  	 });
	      
	      // event audio source
	      btnAudioConvert.setOnClickListener(new View.OnClickListener()
	      {
	  		public void onClick(View view)
	  		{
	  			if (isSuccess)
	  			{
	  				processSpeakForGoogle();
	  				isConvert=true;
	  			}
	  		}
	  	 });
	  	  // event button
	      btnReConvert.setOnClickListener(new View.OnClickListener()
	  			{
	  				public void onClick(View view)
	  				{
	  					isReconvert=true;
	  					final String input=edtDestinationInput.getText().toString();
	  					sourceIndex=spnSourceLanguages.getSelectedItemPosition();
	  					destinationIndex=spnDestinationLanguages.getSelectedItemPosition();
	  					isConvert=false;
	  					spnSourceLanguages.setSelection(destinationIndex);
	  					spnDestinationLanguages.setSelection(sourceIndex);
	  					dialog = ProgressDialog.show(TDict.this,
		    					"Translating...",
		    					"Please wait a moment",true,false);
		    		
		    			searchAdress = new Thread() 
		    			{
		    				public void run()
		    				{
		    					
			  					try 
			  					{	
			  						translatedText = Translate.translate(input,allLanguagesValues.get(destinationIndex),allLanguagesValues.get(sourceIndex));
			  						isSuccess=true;
			  						test=0;
			  						showProgressGoogleTranslate.sendEmptyMessage(0);
			  					} 
			  					catch (Exception ex) 
			  					{
			  						test=1;
			  						showProgressGoogleTranslate.sendEmptyMessage(0);	
			  						  					      
			  					}
		    				}
	    				
	    			};
		    		searchAdress.start();
	  				
	  			}});
	  	 
	  	btnAudioReConvert.setOnClickListener(new View.OnClickListener()
	  	  {
	  			public void onClick(View view)
	  			{
	  				if (isSuccess)
	  					{
	  						processSpeakForGoogle();
	  						isConvert=false;
	  					}
	  			}
	  		});
		
	}
	// Load For ManageDictionary
	public void loadForManageDictionary()
	{
		 layout=new LinearLayout(getApplicationContext());
   	     //Set
     	  radiogroup= new RadioGroup(this);
     	  listFileDb=getDataFileArray();
     	  int indexSelected=-1;
     	  if (listFileDb!=null && listFileDb.size()>0)
     	  {
	      	  for (int i=0;i<listFileDb.size();i++)
	      	  {
	      		  
	      		  RadioButton radio=new RadioButton(this);
	      		  radio.setText(listFileDb.get(i).toString());
	      		  radio.setId(i);
				  if (selectedDb!=null&&selectedDb.equals(listFileDb.get(i)))
					  indexSelected=i;
				  radiogroup.addView(radio, i);
	      		  
	          }
	      	  
	      	  radiogroup.check(indexSelected);
         	  layout.addView(radiogroup);
     	  }
   	
   	      AlertDialog.Builder dialog=new AlertDialog.Builder(TDict.this).setIcon(
	  				R.drawable.manage).setTitle("Select a dictionary")
	  				.setView(layout).setPositiveButton("OK",
	  						new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog,
	  									int whichButton) {
	  								if (listFileDb!=null && listFileDb.size()>0)
	  								{
		  								/* User clicked Yes so do some stuff */
		  								int selectedIndex = radiogroup.getCheckedRadioButtonId();
		  								//selectedDb
		  								if(selectedIndex>=0)
		  								{
		  									selectedDb=listFileDb.get(selectedIndex);
		  									input.setText("");
		  									String pathFileData=getResources().getString(R.string.path_file_data);
		  							       	String fullPathFile=pathFileData+"/"+selectedDb+".db";
		  							       	nameDb=SQLiteDatabase.openDatabase(fullPathFile, null, 1);
		  									setDataForListView(selectedDb);
		  								}
	  								}
	  								
	  							}
	  						}).setNegativeButton("Cancel",
	  						new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog,
	  									int whichButton) {
	  								/* User clicked No so do some stuff */
	  				
	  							}
	  						}).setOnCancelListener(new OnCancelListener() {

	  					public void onCancel(DialogInterface arg0) {

	  						;
	  					}
	  				});
   	  dialog.show();
	}
	public void loadForAbout()
	{
		 
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflate.inflate(R.layout.about,null);
      	   AlertDialog.Builder dialog=new AlertDialog.Builder(TDict.this).setIcon(
	  				R.drawable.tri).setTitle("TDict")
	  				.setView(view).setPositiveButton("OK",
	  						new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog,
	  									int whichButton) {

	  							}
	  						});
   	   dialog.show();
	}
	
}
   