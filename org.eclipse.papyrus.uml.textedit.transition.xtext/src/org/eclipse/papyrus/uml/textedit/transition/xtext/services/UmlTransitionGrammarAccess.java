/*
* generated by Xtext
*/

package org.eclipse.papyrus.uml.textedit.transition.xtext.services;

import com.google.inject.Singleton;
import com.google.inject.Inject;

import org.eclipse.xtext.*;
import org.eclipse.xtext.service.GrammarProvider;
import org.eclipse.xtext.service.AbstractElementFinder.*;

import org.eclipse.xtext.common.services.TerminalsGrammarAccess;

@Singleton
public class UmlTransitionGrammarAccess extends AbstractGrammarElementFinder {
	
	
	public class TransitionRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TransitionRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cGroup.eContents().get(0);
		private final Assignment cTriggersAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		private final RuleCall cTriggersEventRuleParserRuleCall_0_0_0 = (RuleCall)cTriggersAssignment_0_0.eContents().get(0);
		private final Group cGroup_0_1 = (Group)cGroup_0.eContents().get(1);
		private final Keyword cCommaKeyword_0_1_0 = (Keyword)cGroup_0_1.eContents().get(0);
		private final Assignment cTriggersAssignment_0_1_1 = (Assignment)cGroup_0_1.eContents().get(1);
		private final RuleCall cTriggersEventRuleParserRuleCall_0_1_1_0 = (RuleCall)cTriggersAssignment_0_1_1.eContents().get(0);
		private final Assignment cGuardAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cGuardGuardRuleParserRuleCall_1_0 = (RuleCall)cGuardAssignment_1.eContents().get(0);
		private final Assignment cEffectAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cEffectEffectRuleParserRuleCall_2_0 = (RuleCall)cEffectAssignment_2.eContents().get(0);
		
		//TransitionRule:
		//	(triggers+=EventRule ("," triggers+=EventRule)*)? guard=GuardRule? effect=EffectRule?;
		public ParserRule getRule() { return rule; }

		//(triggers+=EventRule ("," triggers+=EventRule)*)? guard=GuardRule? effect=EffectRule?
		public Group getGroup() { return cGroup; }

		//(triggers+=EventRule ("," triggers+=EventRule)*)?
		public Group getGroup_0() { return cGroup_0; }

		//triggers+=EventRule
		public Assignment getTriggersAssignment_0_0() { return cTriggersAssignment_0_0; }

		//EventRule
		public RuleCall getTriggersEventRuleParserRuleCall_0_0_0() { return cTriggersEventRuleParserRuleCall_0_0_0; }

		//("," triggers+=EventRule)*
		public Group getGroup_0_1() { return cGroup_0_1; }

		//","
		public Keyword getCommaKeyword_0_1_0() { return cCommaKeyword_0_1_0; }

		//triggers+=EventRule
		public Assignment getTriggersAssignment_0_1_1() { return cTriggersAssignment_0_1_1; }

		//EventRule
		public RuleCall getTriggersEventRuleParserRuleCall_0_1_1_0() { return cTriggersEventRuleParserRuleCall_0_1_1_0; }

		//guard=GuardRule?
		public Assignment getGuardAssignment_1() { return cGuardAssignment_1; }

		//GuardRule
		public RuleCall getGuardGuardRuleParserRuleCall_1_0() { return cGuardGuardRuleParserRuleCall_1_0; }

		//effect=EffectRule?
		public Assignment getEffectAssignment_2() { return cEffectAssignment_2; }

		//EffectRule
		public RuleCall getEffectEffectRuleParserRuleCall_2_0() { return cEffectEffectRuleParserRuleCall_2_0; }
	}

	public class EventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "EventRule");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cCallOrSignalEventRuleParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cAnyReceiveEventRuleParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cTimeEventRuleParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cChangeEventRuleParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		////////////////////////
		//// EVENTS
		////////////////////////
		//EventRule:
		//	CallOrSignalEventRule | AnyReceiveEventRule | TimeEventRule | ChangeEventRule;
		public ParserRule getRule() { return rule; }

		//CallOrSignalEventRule | AnyReceiveEventRule | TimeEventRule | ChangeEventRule
		public Alternatives getAlternatives() { return cAlternatives; }

		//CallOrSignalEventRule
		public RuleCall getCallOrSignalEventRuleParserRuleCall_0() { return cCallOrSignalEventRuleParserRuleCall_0; }

		//AnyReceiveEventRule
		public RuleCall getAnyReceiveEventRuleParserRuleCall_1() { return cAnyReceiveEventRuleParserRuleCall_1; }

		//TimeEventRule
		public RuleCall getTimeEventRuleParserRuleCall_2() { return cTimeEventRuleParserRuleCall_2; }

		//ChangeEventRule
		public RuleCall getChangeEventRuleParserRuleCall_3() { return cChangeEventRuleParserRuleCall_3; }
	}

	public class CallOrSignalEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "CallOrSignalEventRule");
		private final Assignment cOperationOrSignalAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cOperationOrSignalNamedElementCrossReference_0 = (CrossReference)cOperationOrSignalAssignment.eContents().get(0);
		private final RuleCall cOperationOrSignalNamedElementIDTerminalRuleCall_0_1 = (RuleCall)cOperationOrSignalNamedElementCrossReference_0.eContents().get(1);
		
		//CallOrSignalEventRule:
		//	operationOrSignal=[uml::NamedElement];
		public ParserRule getRule() { return rule; }

		//operationOrSignal=[uml::NamedElement]
		public Assignment getOperationOrSignalAssignment() { return cOperationOrSignalAssignment; }

		//[uml::NamedElement]
		public CrossReference getOperationOrSignalNamedElementCrossReference_0() { return cOperationOrSignalNamedElementCrossReference_0; }

		//ID
		public RuleCall getOperationOrSignalNamedElementIDTerminalRuleCall_0_1() { return cOperationOrSignalNamedElementIDTerminalRuleCall_0_1; }
	}

	public class AnyReceiveEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AnyReceiveEventRule");
		private final Assignment cIsAReceiveEventAssignment = (Assignment)rule.eContents().get(1);
		private final Keyword cIsAReceiveEventAllKeyword_0 = (Keyword)cIsAReceiveEventAssignment.eContents().get(0);
		
		//AnyReceiveEventRule:
		//	isAReceiveEvent="all";
		public ParserRule getRule() { return rule; }

		//isAReceiveEvent="all"
		public Assignment getIsAReceiveEventAssignment() { return cIsAReceiveEventAssignment; }

		//"all"
		public Keyword getIsAReceiveEventAllKeyword_0() { return cIsAReceiveEventAllKeyword_0; }
	}

	public class TimeEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TimeEventRule");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cRelativeTimeEventRuleParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cAbsoluteTimeEventRuleParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//TimeEventRule:
		//	RelativeTimeEventRule | AbsoluteTimeEventRule;
		public ParserRule getRule() { return rule; }

		//RelativeTimeEventRule | AbsoluteTimeEventRule
		public Alternatives getAlternatives() { return cAlternatives; }

		//RelativeTimeEventRule
		public RuleCall getRelativeTimeEventRuleParserRuleCall_0() { return cRelativeTimeEventRuleParserRuleCall_0; }

		//AbsoluteTimeEventRule
		public RuleCall getAbsoluteTimeEventRuleParserRuleCall_1() { return cAbsoluteTimeEventRuleParserRuleCall_1; }
	}

	public class RelativeTimeEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "RelativeTimeEventRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cAfterKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cExprSTRINGTerminalRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		//RelativeTimeEventRule:
		//	"after" expr=STRING;
		public ParserRule getRule() { return rule; }

		//"after" expr=STRING
		public Group getGroup() { return cGroup; }

		//"after"
		public Keyword getAfterKeyword_0() { return cAfterKeyword_0; }

		//expr=STRING
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }

		//STRING
		public RuleCall getExprSTRINGTerminalRuleCall_1_0() { return cExprSTRINGTerminalRuleCall_1_0; }
	}

	public class AbsoluteTimeEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AbsoluteTimeEventRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cAtKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cExprSTRINGTerminalRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		//AbsoluteTimeEventRule:
		//	"at" expr=STRING;
		public ParserRule getRule() { return rule; }

		//"at" expr=STRING
		public Group getGroup() { return cGroup; }

		//"at"
		public Keyword getAtKeyword_0() { return cAtKeyword_0; }

		//expr=STRING
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }

		//STRING
		public RuleCall getExprSTRINGTerminalRuleCall_1_0() { return cExprSTRINGTerminalRuleCall_1_0; }
	}

	public class ChangeEventRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "ChangeEventRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cWhenKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cExpAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cExpSTRINGTerminalRuleCall_1_0 = (RuleCall)cExpAssignment_1.eContents().get(0);
		
		//ChangeEventRule:
		//	"when" exp=STRING;
		public ParserRule getRule() { return rule; }

		//"when" exp=STRING
		public Group getGroup() { return cGroup; }

		//"when"
		public Keyword getWhenKeyword_0() { return cWhenKeyword_0; }

		//exp=STRING
		public Assignment getExpAssignment_1() { return cExpAssignment_1; }

		//STRING
		public RuleCall getExpSTRINGTerminalRuleCall_1_0() { return cExpSTRINGTerminalRuleCall_1_0; }
	}

	public class GuardRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GuardRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cLeftSquareBracketKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintSTRINGTerminalRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		private final Keyword cRightSquareBracketKeyword_2 = (Keyword)cGroup.eContents().get(2);
		
		/////////////////////////
		//// GUARD
		/////////////////////////
		//GuardRule:
		//	"[" constraint=STRING "]";
		public ParserRule getRule() { return rule; }

		//"[" constraint=STRING "]"
		public Group getGroup() { return cGroup; }

		//"["
		public Keyword getLeftSquareBracketKeyword_0() { return cLeftSquareBracketKeyword_0; }

		//constraint=STRING
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }

		//STRING
		public RuleCall getConstraintSTRINGTerminalRuleCall_1_0() { return cConstraintSTRINGTerminalRuleCall_1_0; }

		//"]"
		public Keyword getRightSquareBracketKeyword_2() { return cRightSquareBracketKeyword_2; }
	}

	public class EffectRuleElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "EffectRule");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cSolidusKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cKindAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cKindBehaviorKindEnumRuleCall_1_0 = (RuleCall)cKindAssignment_1.eContents().get(0);
		private final Assignment cBehaviorNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cBehaviorNameIDTerminalRuleCall_2_0 = (RuleCall)cBehaviorNameAssignment_2.eContents().get(0);
		
		/////////////////////////
		//// EFFECT
		/////////////////////////
		//EffectRule:
		//	"/" kind=BehaviorKind behaviorName=ID;
		public ParserRule getRule() { return rule; }

		//"/" kind=BehaviorKind behaviorName=ID
		public Group getGroup() { return cGroup; }

		//"/"
		public Keyword getSolidusKeyword_0() { return cSolidusKeyword_0; }

		//kind=BehaviorKind
		public Assignment getKindAssignment_1() { return cKindAssignment_1; }

		//BehaviorKind
		public RuleCall getKindBehaviorKindEnumRuleCall_1_0() { return cKindBehaviorKindEnumRuleCall_1_0; }

		//behaviorName=ID
		public Assignment getBehaviorNameAssignment_2() { return cBehaviorNameAssignment_2; }

		//ID
		public RuleCall getBehaviorNameIDTerminalRuleCall_2_0() { return cBehaviorNameIDTerminalRuleCall_2_0; }
	}
	
	
	public class BehaviorKindElements extends AbstractEnumRuleElementFinder {
		private final EnumRule rule = (EnumRule) GrammarUtil.findRuleForName(getGrammar(), "BehaviorKind");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final EnumLiteralDeclaration cACTIVITYEnumLiteralDeclaration_0 = (EnumLiteralDeclaration)cAlternatives.eContents().get(0);
		private final Keyword cACTIVITYActivityKeyword_0_0 = (Keyword)cACTIVITYEnumLiteralDeclaration_0.eContents().get(0);
		private final EnumLiteralDeclaration cSTATE_MACHINEEnumLiteralDeclaration_1 = (EnumLiteralDeclaration)cAlternatives.eContents().get(1);
		private final Keyword cSTATE_MACHINEStateMachineKeyword_1_0 = (Keyword)cSTATE_MACHINEEnumLiteralDeclaration_1.eContents().get(0);
		private final EnumLiteralDeclaration cOPAQUE_BEHAVIOREnumLiteralDeclaration_2 = (EnumLiteralDeclaration)cAlternatives.eContents().get(2);
		private final Keyword cOPAQUE_BEHAVIOROpaqueBehaviorKeyword_2_0 = (Keyword)cOPAQUE_BEHAVIOREnumLiteralDeclaration_2.eContents().get(0);
		
		//enum BehaviorKind:
		//	ACTIVITY="Activity" | STATE_MACHINE="StateMachine" | OPAQUE_BEHAVIOR="OpaqueBehavior";
		public EnumRule getRule() { return rule; }

		//ACTIVITY="Activity" | STATE_MACHINE="StateMachine" | OPAQUE_BEHAVIOR="OpaqueBehavior"
		public Alternatives getAlternatives() { return cAlternatives; }

		//ACTIVITY="Activity"
		public EnumLiteralDeclaration getACTIVITYEnumLiteralDeclaration_0() { return cACTIVITYEnumLiteralDeclaration_0; }

		//"Activity"
		public Keyword getACTIVITYActivityKeyword_0_0() { return cACTIVITYActivityKeyword_0_0; }

		//STATE_MACHINE="StateMachine"
		public EnumLiteralDeclaration getSTATE_MACHINEEnumLiteralDeclaration_1() { return cSTATE_MACHINEEnumLiteralDeclaration_1; }

		//"StateMachine"
		public Keyword getSTATE_MACHINEStateMachineKeyword_1_0() { return cSTATE_MACHINEStateMachineKeyword_1_0; }

		//OPAQUE_BEHAVIOR="OpaqueBehavior"
		public EnumLiteralDeclaration getOPAQUE_BEHAVIOREnumLiteralDeclaration_2() { return cOPAQUE_BEHAVIOREnumLiteralDeclaration_2; }

		//"OpaqueBehavior"
		public Keyword getOPAQUE_BEHAVIOROpaqueBehaviorKeyword_2_0() { return cOPAQUE_BEHAVIOROpaqueBehaviorKeyword_2_0; }
	}
	
	private TransitionRuleElements pTransitionRule;
	private EventRuleElements pEventRule;
	private CallOrSignalEventRuleElements pCallOrSignalEventRule;
	private AnyReceiveEventRuleElements pAnyReceiveEventRule;
	private TimeEventRuleElements pTimeEventRule;
	private RelativeTimeEventRuleElements pRelativeTimeEventRule;
	private AbsoluteTimeEventRuleElements pAbsoluteTimeEventRule;
	private ChangeEventRuleElements pChangeEventRule;
	private GuardRuleElements pGuardRule;
	private EffectRuleElements pEffectRule;
	private BehaviorKindElements unknownRuleBehaviorKind;
	
	private final GrammarProvider grammarProvider;

	private TerminalsGrammarAccess gaTerminals;

	@Inject
	public UmlTransitionGrammarAccess(GrammarProvider grammarProvider,
		TerminalsGrammarAccess gaTerminals) {
		this.grammarProvider = grammarProvider;
		this.gaTerminals = gaTerminals;
	}
	
	public Grammar getGrammar() {	
		return grammarProvider.getGrammar(this);
	}
	

	public TerminalsGrammarAccess getTerminalsGrammarAccess() {
		return gaTerminals;
	}

	
	//TransitionRule:
	//	(triggers+=EventRule ("," triggers+=EventRule)*)? guard=GuardRule? effect=EffectRule?;
	public TransitionRuleElements getTransitionRuleAccess() {
		return (pTransitionRule != null) ? pTransitionRule : (pTransitionRule = new TransitionRuleElements());
	}
	
	public ParserRule getTransitionRuleRule() {
		return getTransitionRuleAccess().getRule();
	}

	////////////////////////
	//// EVENTS
	////////////////////////
	//EventRule:
	//	CallOrSignalEventRule | AnyReceiveEventRule | TimeEventRule | ChangeEventRule;
	public EventRuleElements getEventRuleAccess() {
		return (pEventRule != null) ? pEventRule : (pEventRule = new EventRuleElements());
	}
	
	public ParserRule getEventRuleRule() {
		return getEventRuleAccess().getRule();
	}

	//CallOrSignalEventRule:
	//	operationOrSignal=[uml::NamedElement];
	public CallOrSignalEventRuleElements getCallOrSignalEventRuleAccess() {
		return (pCallOrSignalEventRule != null) ? pCallOrSignalEventRule : (pCallOrSignalEventRule = new CallOrSignalEventRuleElements());
	}
	
	public ParserRule getCallOrSignalEventRuleRule() {
		return getCallOrSignalEventRuleAccess().getRule();
	}

	//AnyReceiveEventRule:
	//	isAReceiveEvent="all";
	public AnyReceiveEventRuleElements getAnyReceiveEventRuleAccess() {
		return (pAnyReceiveEventRule != null) ? pAnyReceiveEventRule : (pAnyReceiveEventRule = new AnyReceiveEventRuleElements());
	}
	
	public ParserRule getAnyReceiveEventRuleRule() {
		return getAnyReceiveEventRuleAccess().getRule();
	}

	//TimeEventRule:
	//	RelativeTimeEventRule | AbsoluteTimeEventRule;
	public TimeEventRuleElements getTimeEventRuleAccess() {
		return (pTimeEventRule != null) ? pTimeEventRule : (pTimeEventRule = new TimeEventRuleElements());
	}
	
	public ParserRule getTimeEventRuleRule() {
		return getTimeEventRuleAccess().getRule();
	}

	//RelativeTimeEventRule:
	//	"after" expr=STRING;
	public RelativeTimeEventRuleElements getRelativeTimeEventRuleAccess() {
		return (pRelativeTimeEventRule != null) ? pRelativeTimeEventRule : (pRelativeTimeEventRule = new RelativeTimeEventRuleElements());
	}
	
	public ParserRule getRelativeTimeEventRuleRule() {
		return getRelativeTimeEventRuleAccess().getRule();
	}

	//AbsoluteTimeEventRule:
	//	"at" expr=STRING;
	public AbsoluteTimeEventRuleElements getAbsoluteTimeEventRuleAccess() {
		return (pAbsoluteTimeEventRule != null) ? pAbsoluteTimeEventRule : (pAbsoluteTimeEventRule = new AbsoluteTimeEventRuleElements());
	}
	
	public ParserRule getAbsoluteTimeEventRuleRule() {
		return getAbsoluteTimeEventRuleAccess().getRule();
	}

	//ChangeEventRule:
	//	"when" exp=STRING;
	public ChangeEventRuleElements getChangeEventRuleAccess() {
		return (pChangeEventRule != null) ? pChangeEventRule : (pChangeEventRule = new ChangeEventRuleElements());
	}
	
	public ParserRule getChangeEventRuleRule() {
		return getChangeEventRuleAccess().getRule();
	}

	/////////////////////////
	//// GUARD
	/////////////////////////
	//GuardRule:
	//	"[" constraint=STRING "]";
	public GuardRuleElements getGuardRuleAccess() {
		return (pGuardRule != null) ? pGuardRule : (pGuardRule = new GuardRuleElements());
	}
	
	public ParserRule getGuardRuleRule() {
		return getGuardRuleAccess().getRule();
	}

	/////////////////////////
	//// EFFECT
	/////////////////////////
	//EffectRule:
	//	"/" kind=BehaviorKind behaviorName=ID;
	public EffectRuleElements getEffectRuleAccess() {
		return (pEffectRule != null) ? pEffectRule : (pEffectRule = new EffectRuleElements());
	}
	
	public ParserRule getEffectRuleRule() {
		return getEffectRuleAccess().getRule();
	}

	//enum BehaviorKind:
	//	ACTIVITY="Activity" | STATE_MACHINE="StateMachine" | OPAQUE_BEHAVIOR="OpaqueBehavior";
	public BehaviorKindElements getBehaviorKindAccess() {
		return (unknownRuleBehaviorKind != null) ? unknownRuleBehaviorKind : (unknownRuleBehaviorKind = new BehaviorKindElements());
	}
	
	public EnumRule getBehaviorKindRule() {
		return getBehaviorKindAccess().getRule();
	}

	//terminal ID:
	//	"^"? ("a".."z" | "A".."Z" | "_") ("a".."z" | "A".."Z" | "_" | "0".."9")*;
	public TerminalRule getIDRule() {
		return gaTerminals.getIDRule();
	} 

	//terminal INT returns ecore::EInt:
	//	"0".."9"+;
	public TerminalRule getINTRule() {
		return gaTerminals.getINTRule();
	} 

	//terminal STRING:
	//	"\"" ("\\" ("b" | "t" | "n" | "f" | "r" | "u" | "\"" | "\'" | "\\") | !("\\" | "\""))* "\"" | "\'" ("\\" ("b" | "t" |
	//	"n" | "f" | "r" | "u" | "\"" | "\'" | "\\") | !("\\" | "\'"))* "\'";
	public TerminalRule getSTRINGRule() {
		return gaTerminals.getSTRINGRule();
	} 

	//terminal ML_COMMENT:
	//	"/ *"->"* /";
	public TerminalRule getML_COMMENTRule() {
		return gaTerminals.getML_COMMENTRule();
	} 

	//terminal SL_COMMENT:
	//	"//" !("\n" | "\r")* ("\r"? "\n")?;
	public TerminalRule getSL_COMMENTRule() {
		return gaTerminals.getSL_COMMENTRule();
	} 

	//terminal WS:
	//	(" " | "\t" | "\r" | "\n")+;
	public TerminalRule getWSRule() {
		return gaTerminals.getWSRule();
	} 

	//terminal ANY_OTHER:
	//	.;
	public TerminalRule getANY_OTHERRule() {
		return gaTerminals.getANY_OTHERRule();
	} 
}
