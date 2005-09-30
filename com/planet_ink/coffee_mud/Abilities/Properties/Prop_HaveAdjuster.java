package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

import java.util.*;

// this ability is the very picture of the infectuous msg.
// It lobs itself onto other qualified objects, and withdraws
// again when it will.  Don't lothe the HaveAdjuster, LOVE IT.
/* 
   Copyright 2000-2005 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Prop_HaveAdjuster extends Property
{
	public String ID() { return "Prop_HaveAdjuster"; }
	public String name(){ return "Adjustments to stats when owned";}
	protected int canAffectCode(){return Ability.CAN_ITEMS;}
	public boolean bubbleAffect(){return true;}
	private CharStats adjCharStats=null;
	private CharState adjCharState=null;
	private EnvStats adjEnvStats=null;
	boolean gotClass=false;
	boolean gotRace=false;
	boolean gotSex=false;
    private Vector mask=new Vector();
    
    public static String buildMask(String newText, Vector mask)
    {
        int maskindex=newText.toUpperCase().indexOf("MASK=");
        if(maskindex>0)
        {
            String maskStr=newText.substring(maskindex+5).trim();
            if(maskStr.length()>0)
                Util.addToVector(MUDZapper.zapperCompile(maskStr),mask);
            newText=newText.substring(0,maskindex).trim();
        }
        return newText;
    }

    public static String[] separateMask(String newText)
    {
        String[] strs=new String[2];
        int maskindex=newText.toUpperCase().indexOf("MASK=");
        if(maskindex>0)
        {
            strs[1]=newText.substring(maskindex+5).trim();
            strs[0]=newText.substring(0,maskindex).trim();
        }
        return strs;
    }
    
	public static int setAdjustments(String newText, 
                                     EnvStats adjEnvStats, 
                                     CharStats adjCharStats, 
                                     CharState adjCharState,
                                     Vector mask)
	{
		boolean gotClass=false;
		boolean gotRace=false;
		boolean gotSex=false;
        
        newText=buildMask(newText,mask);
        
		adjEnvStats.setAbility(Util.getParmPlus(newText,"abi"));
		adjEnvStats.setArmor(Util.getParmPlus(newText,"arm"));
		adjEnvStats.setAttackAdjustment(Util.getParmPlus(newText,"att"));
		adjEnvStats.setDamage(Util.getParmPlus(newText,"dam"));
		adjEnvStats.setDisposition(Util.getParmPlus(newText,"dis"));
		adjEnvStats.setLevel(Util.getParmPlus(newText,"lev"));
		adjEnvStats.setRejuv(Util.getParmPlus(newText,"rej"));
		adjEnvStats.setSensesMask(Util.getParmPlus(newText,"sen"));
		adjEnvStats.setSpeed(Util.getParmDoublePlus(newText,"spe"));
		adjEnvStats.setWeight(Util.getParmPlus(newText,"wei"));
		adjEnvStats.setHeight(Util.getParmPlus(newText,"hei"));

		String val=Util.getParmStr(newText,"gen","").toUpperCase();
		if((val.length()>0)&&((val.charAt(0)=='M')||(val.charAt(0)=='F')||(val.charAt(0)=='N')))
		{
			adjCharStats.setStat(CharStats.GENDER,val.charAt(0));
			gotSex=true;
		}

		val=Util.getParmStr(newText,"cla","").toUpperCase();
		if((val.length()>0)&&(CMClass.findCharClass(val)!=null)&&(!val.equalsIgnoreCase("Archon")))
		{
			gotClass=true;
			adjCharStats.setCurrentClass(CMClass.findCharClass(val));
		}
		val=Util.getParmStr(newText,"rac","").toUpperCase();
		if((val.length()>0)&&(CMClass.getRace(val)!=null))
		{
			gotRace=true;
			adjCharStats.setMyRace(CMClass.getRace(val));
		}
		adjCharStats.setStat(CharStats.STRENGTH,Util.getParmPlus(newText,"str"));
		adjCharStats.setStat(CharStats.WISDOM,Util.getParmPlus(newText,"wis"));
		adjCharStats.setStat(CharStats.CHARISMA,Util.getParmPlus(newText,"cha"));
		adjCharStats.setStat(CharStats.CONSTITUTION,Util.getParmPlus(newText,"con"));
		adjCharStats.setStat(CharStats.DEXTERITY,Util.getParmPlus(newText,"dex"));
		adjCharStats.setStat(CharStats.INTELLIGENCE,Util.getParmPlus(newText,"int"));
		adjCharStats.setStat(CharStats.MAX_STRENGTH_ADJ,Util.getParmPlus(newText,"maxstr"));
		adjCharStats.setStat(CharStats.MAX_WISDOM_ADJ,Util.getParmPlus(newText,"maxwis"));
		adjCharStats.setStat(CharStats.MAX_CHARISMA_ADJ,Util.getParmPlus(newText,"maxcha"));
		adjCharStats.setStat(CharStats.MAX_CONSTITUTION_ADJ,Util.getParmPlus(newText,"maxcon"));
		adjCharStats.setStat(CharStats.MAX_DEXTERITY_ADJ,Util.getParmPlus(newText,"maxdex"));
		adjCharStats.setStat(CharStats.MAX_INTELLIGENCE_ADJ,Util.getParmPlus(newText,"maxint"));

		adjCharState.setHitPoints(Util.getParmPlus(newText,"hit"));
		adjCharState.setHunger(Util.getParmPlus(newText,"hun"));
		adjCharState.setMana(Util.getParmPlus(newText,"man"));
		adjCharState.setMovement(Util.getParmPlus(newText,"mov"));
		adjCharState.setThirst(Util.getParmPlus(newText,"thi"));
		return ((gotClass?1:0)+(gotRace?2:0)+(gotSex?4:0));
	}

	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		this.adjCharStats=new DefaultCharStats();
		this.adjCharState=new DefaultCharState();
		this.adjEnvStats=new DefaultEnvStats();
        this.mask=new Vector();
		int gotit=setAdjustments(newText,adjEnvStats,adjCharStats,adjCharState,mask);
		gotClass=((gotit&1)==1);
		gotRace=((gotit&2)==2);
		gotSex=((gotit&4)==4);
	}

	public static void envStuff(EnvStats affectableStats, EnvStats adjEnvStats)
	{
		affectableStats.setAbility(affectableStats.ability()+adjEnvStats.ability());
		affectableStats.setArmor(affectableStats.armor()+adjEnvStats.armor());
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+adjEnvStats.attackAdjustment());
		affectableStats.setDamage(affectableStats.damage()+adjEnvStats.damage());
		affectableStats.setDisposition(affectableStats.disposition()|adjEnvStats.disposition());
		affectableStats.setLevel(affectableStats.level()+adjEnvStats.level());
		affectableStats.setRejuv(affectableStats.rejuv()+adjEnvStats.rejuv());
		affectableStats.setSensesMask(affectableStats.sensesMask()|adjEnvStats.sensesMask());
		affectableStats.setSpeed(affectableStats.speed()+adjEnvStats.speed());
		affectableStats.setWeight(affectableStats.weight()+adjEnvStats.weight());
		affectableStats.setHeight(affectableStats.height()+adjEnvStats.height());
	}

	private void ensureStarted()
	{
		if(adjCharStats==null)
			setMiscText(text());
	}

	public void affectEnvStats(Environmental affectedMOB, EnvStats affectableStats)
	{
		ensureStarted();
		if((affectedMOB!=null)
		&&(affectedMOB instanceof MOB)
		&&(affected!=null)
		&&(affected instanceof Item)
		&&(!((Item)affected).amDestroyed())
        &&((mask.size()==0)||(MUDZapper.zapperCheckReal(mask,affectedMOB))))
			envStuff(affectableStats,adjEnvStats);
		super.affectEnvStats(affectedMOB,affectableStats);
	}

	public static void adjCharStats(CharStats affectedStats,
									boolean gotClass,
									boolean gotRace,
									boolean gotSex,
									CharStats adjCharStats)
	{
		affectedStats.setStat(CharStats.CHARISMA,affectedStats.getStat(CharStats.CHARISMA)+adjCharStats.getStat(CharStats.CHARISMA));
		affectedStats.setStat(CharStats.CONSTITUTION,affectedStats.getStat(CharStats.CONSTITUTION)+adjCharStats.getStat(CharStats.CONSTITUTION));
		affectedStats.setStat(CharStats.DEXTERITY,affectedStats.getStat(CharStats.DEXTERITY)+adjCharStats.getStat(CharStats.DEXTERITY));
		if(gotSex)
			affectedStats.setStat(CharStats.GENDER,adjCharStats.getStat(CharStats.GENDER));
		affectedStats.setStat(CharStats.INTELLIGENCE,affectedStats.getStat(CharStats.INTELLIGENCE)+adjCharStats.getStat(CharStats.INTELLIGENCE));
		if(gotClass)
			affectedStats.setCurrentClass(adjCharStats.getCurrentClass());
		if(gotRace)
			affectedStats.setMyRace(adjCharStats.getMyRace());
		affectedStats.setStat(CharStats.STRENGTH,affectedStats.getStat(CharStats.STRENGTH)+adjCharStats.getStat(CharStats.STRENGTH));
		affectedStats.setStat(CharStats.WISDOM,affectedStats.getStat(CharStats.WISDOM)+adjCharStats.getStat(CharStats.WISDOM));
	}

	public static void adjCharState(CharState affectedState,
									CharState adjCharState)
	{
		affectedState.setHitPoints(affectedState.getHitPoints()+adjCharState.getHitPoints());
		affectedState.setHunger(affectedState.getHunger()+adjCharState.getHunger());
		affectedState.setMana(affectedState.getMana()+adjCharState.getMana());
		affectedState.setMovement(affectedState.getMovement()+adjCharState.getMovement());
		affectedState.setThirst(affectedState.getThirst()+adjCharState.getThirst());
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		ensureStarted();
        if((mask.size()==0)||(MUDZapper.zapperCheckReal(mask,affectedMOB)))
    		adjCharStats(affectedStats,gotClass,gotRace,gotSex,adjCharStats);
		super.affectCharStats(affectedMOB,affectedStats);
	}
	public void affectCharState(MOB affectedMOB, CharState affectedState)
	{
		ensureStarted();
        if((mask.size()==0)||(MUDZapper.zapperCheckReal(mask,affectedMOB)))
    		adjCharState(affectedState,adjCharState);
		super.affectCharState(affectedMOB,affectedState);
	}

	public static String fixAccoutingsWithMask(String id)
	{
        Vector mask=new Vector();
        String[] strs=separateMask(id);
        id=strs[0];
		int x=id.toUpperCase().indexOf("ARM");
		for(StringBuffer ID=new StringBuffer(id);((x>0)&&(x<id.length()));x++)
			if(id.charAt(x)=='-')
			{
				ID.setCharAt(x,'+');
				id=ID.toString();
				break;
			}
			else
			if(id.charAt(x)=='+')
			{
				ID.setCharAt(x,'-');
				id=ID.toString();
				break;
			}
			else
			if(Character.isDigit(id.charAt(x)))
				break;
		x=id.toUpperCase().indexOf("DIS");
		if(x>=0)
		{
			long val=Util.getParmPlus(id,"dis");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.dispositionsVerb.length;num++)
					if(Util.bset(val,Util.pow(2,num)))
						middle.append(EnvStats.dispositionsVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		x=id.toUpperCase().indexOf("SEN");
		if(x>=0)
		{
			long val=Util.getParmPlus(id,"sen");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.sensesVerb.length;num++)
					if(Util.bset(val,Util.pow(2,num)))
						middle.append(EnvStats.sensesVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
        if(strs[1].length()>0)
            id+="\n\rRestrictions: "+MUDZapper.zapperDesc(strs[1]);
		return id;
	}

	public String accountForYourself()
	{
		return fixAccoutingsWithMask("Affects the owner: "+text());
	}
}
