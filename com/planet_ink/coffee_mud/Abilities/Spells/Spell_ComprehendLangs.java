package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Spell_ComprehendLangs extends Spell
{
	public String ID() { return "Spell_ComprehendLangs"; }
	public String name(){return "Comprehend Languages";}
	public String displayText(){return "(Comprehend Languages)";}
	public int quality(){return BENEFICIAL_SELF;};
	protected int canAffectCode(){return CAN_MOBS;}
	public Environmental newInstance(){	return new Spell_ComprehendLangs();}
	public int classificationCode(){	return Ability.SPELL|Ability.DOMAIN_DIVINATION;}


	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell("You no longer feel so comprehensive.");
	}

	protected String getMsgFromAffect(String msg)
	{
		if(msg==null) return null;
		int start=msg.indexOf("'");
		int end=msg.lastIndexOf("'");
		if((start>0)&&(end>start))
			return msg.substring(start+1,end).trim();
		return null;
	}
	protected String subStitute(String affmsg, String msg)
	{
		if(affmsg==null) return null;
		int start=affmsg.indexOf("'");
		int end=affmsg.lastIndexOf("'");
		if((start>0)&&(end>start))
			return affmsg.substring(0,start+1)+msg+affmsg.substring(end);
		return affmsg;
	}
	public void affect(Environmental myHost, Affect affect)
	{
		super.affect(myHost,affect);
		if((affected instanceof MOB)
		&&(!affect.amISource((MOB)affected))
		&&((affect.sourceMinor()==Affect.TYP_SPEAK)
		   ||(affect.sourceMinor()==Affect.TYP_TELL)
		   ||(Util.bset(affect.sourceCode(),Affect.MASK_CHANNEL)))
		&&(affect.tool() !=null)
		&&(affect.sourceMessage()!=null)
		&&(affect.tool() instanceof Ability)
		&&(((Ability)affect.tool()).classificationCode()==Ability.LANGUAGE)
		&&(((MOB)affected).fetchAffect(affect.tool().ID())==null))
		{
			String msg=this.getMsgFromAffect(affect.sourceMessage());
			if(msg!=null)
			{
				if(Util.bset(affect.sourceCode(),Affect.MASK_CHANNEL))
					affect.addTrailerMsg(new FullMsg(affect.source(),null,null,Affect.NO_EFFECT,Affect.NO_EFFECT,affect.othersCode(),this.subStitute(affect.othersMessage(),msg)+" (translated from "+ID()+")"));
				else
				if(affect.amITarget(null)&&(affect.targetMessage()!=null))
					affect.addTrailerMsg(new FullMsg(affect.source(),(MOB)affected,null,Affect.NO_EFFECT,affect.targetCode(),Affect.NO_EFFECT,this.subStitute(affect.targetMessage(),msg)+" (translated from "+((Ability)affect.tool()).ID()+")"));
				else
				if(!affect.amITarget(null)&&(affect.othersMessage()!=null))
					affect.addTrailerMsg(new FullMsg(affect.source(),(MOB)affected,null,Affect.NO_EFFECT,affect.othersCode(),Affect.NO_EFFECT,this.subStitute(affect.othersMessage(),msg)+" (translated from "+((Ability)affect.tool()).ID()+")"));
			}
		}
	}


	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		MOB target=mob;
		if(target==null) return false;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB)) 
			target=(MOB)givenTarget;
		if(target.fetchAffect(ID())!=null)
		{
			mob.tell("You already have comprehension.");
			return false;
		}

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			FullMsg msg=new FullMsg(mob,target,this,affectType(auto),auto?"<T-NAME> feel(s) more comprehrending.":"^S<S-NAME> invoke(s) the power of comprehension!^?");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> attempt(s) to invoke a spell, but fail(s) miserably.");

		// return whether it worked
		return success;
	}
}