package de.upb.ds.surnia.preprocessing;

import de.upb.ds.surnia.preprocessing.model.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides Combines
 */
public class TokenMerger {

  /**
   * Tries to integrate the given token into the given list of tokens. If the text of the new token
   * matches with two or more tokens, these will be merged in the final result.
   *
   * @param oldTokens List of tokens.
   * @param newToken newly produced token.
   * @return List of tokens where the resource tokens are combined and linked.
   */
  public List<Token> integrateToken(List<Token> oldTokens, Token newToken) {
      List<Token> linkedTokens = new ArrayList<>();
      HashMap<String, Integer> posTags = new HashMap<>();
      boolean appearanceFound = false;
      boolean entityAdded = false;
      for (Token token : oldTokens) {
          if (newToken.getText().toLowerCase().contains(token.getText())&& token.getUris().isEmpty()) {
              if (!appearanceFound) {
                  appearanceFound = true;
              }
              if (posTags.containsKey(token.getType())) {
                  posTags.put(token.getType(), posTags.get(token.getType()) + 1);
              } else {
                  posTags.put(token.getType(), 1);
              }
              if (appearanceFound && !entityAdded) {
                  entityAdded = true;
                  // if new token already has a POS-tag, we can assume that it is better
                  String posTag = newToken.getType() != null ? newToken.getType() : choosePosTag(posTags);
                  linkedTokens.add(new Token(newToken.getText(), posTag, newToken.getUris()));
              }
          } else {
              if (!appearanceFound || entityAdded) {
                  linkedTokens.add(token);
              }
          }
      }
      return linkedTokens;
  }

  /**
   * This method chooses a POS-tag based on the biggest coverage, i.e. if 3 tokens where merged in
   * {@link #integrateToken(List, Token)} and 2 of the 3 tokens have the same POS-tag, that POS-tag
   * will be chosen for the new token.
   *
   * @param posTags Map of the POS-tags and the number of their occurences.
   */
  private String choosePosTag(Map<String, Integer> posTags) {
    String posTag = "NNP";
    int max = 0;
    for (String tag : posTags.keySet()) {
      if (posTags.get(tag) > max) {
        max = posTags.get(tag);
        posTag = tag;
      }
    }
    return posTag;
  }

}
