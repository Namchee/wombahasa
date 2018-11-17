package app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarEvaluator {
    
    private String sentence;
    private HashSet<String> dictionary;
    
    /*
     * Set of changeable rules
     */
    private String verb;
    private String noun;
    
    /*
     * Final set of rules
     * IT WON'T BE CHANGED
     */
    private final String ARTICLE = "(a|the)";
    private final String ACTOR = "(" + ARTICLE + " )?" + noun;
    private final String ACTIVE_LIST = "(" + ACTOR + " and )*" + ACTOR;
    private final String ACTION = ACTIVE_LIST + " " + verb + " " + ACTIVE_LIST;
    private final String STATEMENT = "(" + ACTION + " , )*" + ACTION;
    
    private HashMap<String, ArrayList<String>> misspellings;
    
    private boolean unknown;
    
    public GrammarEvaluator () {
        this(null, null);
    }
    
    public GrammarEvaluator (HashSet<String> dictionary, String sentence) {
        this.dictionary = dictionary;
        this.sentence = sentence;
        this.verb = "";
        this.noun = "";
        this.misspellings = new HashMap<>();
        this.unknown = false;
    }
    
    /**
     * setDictionary
     * Self explanatory
     * @param words
     */
    public void setDictionary (ArrayList<String> words) {
        for (String x : words) {
           this.dictionary.add(x);
        }
    }
    
    /**
     * setSentence
     * Self explanatory
     * @param sentence
     */
    public void setSentence (String sentence) {
        this.sentence = sentence;
    }
    
    /**
     * setRules
     * Specifically set nouns and verbs
     * @param nouns
     * @param verbs
     */
    public void setRules (ArrayList<String> nouns, ArrayList<String> verbs) {
        this.noun = "(";
        for (int i = 0; i < nouns.size(); i++) {
            if (i > 0) this.noun += "|";
            this.noun += nouns.get(i);
        }
        this.noun += ")";
        this.verb = "(";
        for (int i = 0; i < verbs.size(); i++) {
            if (i > 0) this.verb += "|";
            this.verb += verbs.get(i);
        }
        this.verb += ")";
    }
    
    /**
     * spellCheck ()
     * Check if ALL words in the sentence is available on
     * the list of known words
     * @return true if there's no misspellings, false otherwise
     */
    public boolean spellCheck () {
        String[] token = this.sentence.split("\\s+");
        boolean flag = true;
        
        for (int i = 0; i < token.length && flag; i++) {
            if (!token[i].equals(",") && !this.dictionary.contains(token[i])) flag = false; 
        }
        
        return flag;
    }
    
    /**
     * grammarCheck()
     * Self explanatory
     * MAKE SURE THAT THE SPELLING IS CORRECT TOO!
     * @return true of the sentence is grammatically correct, false otherwise
     */
    public boolean grammarCheck () {
        return this.grammarCheck(this.sentence);
    }
    
    public boolean grammarCheck (String sentence) {
        return sentence.matches(STATEMENT);
    }
    
    /**
     * generateMisspellings
     * Give the list of misspellings from the sentence
     */
    public void generateMisspellings () {
        String[] token = this.sentence.split("\\s+");
            
        for (int i = 0; i < token.length; i++) {
            if (!token[i].equals(",") && !this.dictionary.contains(token[i])) {
                ArrayList<String> possibilities = new ArrayList<String>();
                   
                // try to guess what word is it
                    
                // try to insert a char
                char arr[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
                for (int x = 0; x < arr.length; x++) {
                    for (int j = 0; i <= token[i].length(); j++) {
                        StringBuilder str = new StringBuilder(token[i]);
                            str.insert(j, arr[x]);
                            if (this.dictionary.contains(str.toString())) possibilities.add(str.toString());
                        }
                    }
                    
                    // try to delete a char
                    for (int j = 0; j < token[i].length(); j++) {
                        StringBuilder str = new StringBuilder(token[i]);
                        str.deleteCharAt(j);
                        if (this.dictionary.contains(str.toString())) possibilities.add(str.toString());
                    }
                    
                    // try to swap char
                    for (int j = 0; j < token[i].length() - 1; j++) {
                        String[] str = token[i].split("");
                        String temp = str[j];
                        str[j] = str[j + 1];
                        str[j + 1] = temp;
                        String swapped = String.join("", str);
                        if (this.dictionary.contains(swapped)) possibilities.add(swapped);
                    }
                    
                    // try to replace char
                    for (int x = 0; x < arr.length; x++) {
                        for (int j = 0; j < token[i].length(); j++) {
                            StringBuilder str = new StringBuilder(token[i]);
                            str.setCharAt(j, arr[x]);
                            if (this.dictionary.contains(str.toString())) possibilities.add(str.toString());
                        }
                    }
                    
                    if (possibilities.isEmpty()) this.unknown = true;
                    this.misspellings.put(token[i], possibilities);
                }
            }
    }
    
    /**
     * findNouns
     * Find all nouns in sentence
     * @return list of nouns
     */
    public ArrayList<String> findNouns () {
        ArrayList<String> nouns = new ArrayList<String>();
        Pattern pattern = Pattern.compile(this.noun);
        Matcher matcher = pattern.matcher(this.sentence);
        
        while (matcher.find()) {
            nouns.add(matcher.group());
        }
        
        return nouns;
    }
    
    /**
     * findVerbs
     * Find all verbs in sentence
     * @return list of verbs
     */
    public ArrayList<String> findVerbs () {
        ArrayList<String> verbs = new ArrayList<String>();
        Pattern pattern = Pattern.compile(this.verb);
        Matcher matcher = pattern.matcher(this.sentence);
        
        while (matcher.find()) {
            verbs.add(matcher.group());
        }
        
        return verbs;
    }
    
    /**
     * findActors
     * Find all actors in sentence
     * @return list of actors
     */
    public ArrayList<String> findActors () {
        ArrayList<String> actors = new ArrayList<String>();
        Pattern pattern = Pattern.compile(ACTOR);
        Matcher matcher = pattern.matcher(this.sentence);
        
        while (matcher.find()) {
            actors.add(matcher.group());
        }
        
        return actors;
    }
    
    /**
     * findActiveLists
     * Find all active lists in sentence
     * @return list of active_lists
     */
    public ArrayList<String> findActiveLists () {
        ArrayList<String> active_lists = new ArrayList<String>();
        Pattern pattern = Pattern.compile(ACTIVE_LIST);
        Matcher matcher = pattern.matcher(this.sentence);
        
        while (matcher.find()) {
            active_lists.add(matcher.group());
        }
        
        return active_lists;
    }
    
    /**
     * findActions
     * Find all actions in sentence
     * @return list of actions
     */
    public ArrayList<String> findActions () {
        ArrayList<String> actions = new ArrayList<String>();
        Pattern pattern = Pattern.compile(ACTION);
        Matcher matcher = pattern.matcher(this.sentence);
        
        while (matcher.find()) {
            actions.add(matcher.group());
        }
        
        return actions;
    }
    
    /**
     * guessCorrectSentences
     * Try to guess correct sentences
     * FROM misspellings, otherwise, cannot guess
     * NEVER TRY TO GUESS WHEN unknown IS TRUE!
     * @return list of guesses
     */
    public ArrayList<String> guessCorrectSentences () {
        ArrayList<String> guesses = new ArrayList<String>();
        
        String[] token = this.sentence.split("\\s+");
        String[] copy = new String[token.length];
        
        for (int i = 0; i < token.length; i++) {
            if (this.misspellings.containsKey(token[i])) {
                for (String x : this.misspellings.get(token[i])) {
                    System.arraycopy(token, 0, copy, 0, token.length);
                    copy[i] = x;
                    String fixedSentence = String.join(" ", copy);
                    if (this.grammarCheck(fixedSentence)) guesses.add(fixedSentence);
                }
            }
        }
        
        return guesses;
    }
    
    /**
     * getUnknown
     * Check if there's unguessable word in the sentence
     * @return true if there's unguessable word, false otherwise
     */
    public boolean getUnknown () {
        return this.unknown;
    }
}

