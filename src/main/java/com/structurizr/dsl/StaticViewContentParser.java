package com.structurizr.dsl;

import com.structurizr.model.*;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.StaticView;

import java.util.Set;

final class StaticViewContentParser extends ViewContentParser {

    private static final int FIRST_IDENTIFIER_INDEX = 1;
    private static final int RELATIONSHIP_IDENTIFIER_INDEX = 2;

    private static final String WILDCARD = "*";
    private static final String RELATIONSHIP = "->";

    void parseInclude(StaticViewDslContext context, Tokens tokens) {
        if (!tokens.includes(FIRST_IDENTIFIER_INDEX)) {
            throw new RuntimeException("Expected: include <*|identifier> [identifier...] or include <*|identifier> -> <*|identifier>");
        }

        StaticView view = context.getView();

        if (tokens.size() == 4 && tokens.get(RELATIONSHIP_IDENTIFIER_INDEX).equals(RELATIONSHIP)) {
            // include <*|identifier> -> <*|identifier>
            String sourceElementIdentifier = tokens.get(RELATIONSHIP_IDENTIFIER_INDEX - 1);
            String destinationElementIdentifier = tokens.get(RELATIONSHIP_IDENTIFIER_INDEX + 1);

            Set<Relationship> relationships = findRelationships(context, sourceElementIdentifier, destinationElementIdentifier);
            for (Relationship relationship : relationships) {
                context.getView().add(relationship);
            }
        } else if (tokens.contains(WILDCARD)) {
            // include *
            view.addDefaultElements();
        } else {
            // include <identifier> [identifier...]
            for (int i = FIRST_IDENTIFIER_INDEX; i < tokens.size(); i++) {
                String token = tokens.get(i);

                // assume the token is an identifier
                Element element = context.getElement(token);
                Relationship relationship = context.getRelationship(token);
                if (element == null && relationship == null) {
                    throw new RuntimeException("The element/relationship \"" + token + "\" does not exist");
                }

                if (element != null) {
                    if (element instanceof CustomElement) {
                        view.add((CustomElement) element);
                    } else if (element instanceof Person) {
                        view.add((Person) element);
                    } else if (element instanceof SoftwareSystem) {
                        view.add((SoftwareSystem) element);
                    } else if (element instanceof Container && (view instanceof ContainerView)) {
                        ((ContainerView) view).add((Container) element);
                    } else if (element instanceof Container && (view instanceof ComponentView)) {
                        ((ComponentView) view).add((Container) element);
                    } else if (element instanceof Component && (view instanceof ComponentView)) {
                        ((ComponentView) view).add((Component) element);
                    } else {
                        throw new RuntimeException("The element \"" + token + "\" can not be added to this type of view");
                    }
                }

                if (relationship != null) {
                    view.add(relationship);
                }
            }
        }
    }

    void parseExclude(StaticViewDslContext context, Tokens tokens) {
        if (!tokens.includes(FIRST_IDENTIFIER_INDEX)) {
            throw new RuntimeException("Expected: exclude <identifier> [identifier...] or exclude <*|identifier> -> <*|identifier>");
        }

        StaticView view = context.getView();

        if (tokens.size() == 4 && tokens.get(RELATIONSHIP_IDENTIFIER_INDEX).equals(RELATIONSHIP)) {
            // exclude <*|identifier> -> <*|identifier>
            String sourceElementIdentifier = tokens.get(RELATIONSHIP_IDENTIFIER_INDEX - 1);
            String destinationElementIdentifier = tokens.get(RELATIONSHIP_IDENTIFIER_INDEX + 1);

            Set<Relationship> relationships = findRelationships(context, sourceElementIdentifier, destinationElementIdentifier);
            for (Relationship relationship : relationships) {
                context.getView().remove(relationship);
            }
        } else {
            // exclude <identifier> [identifier...]
            for (int i = FIRST_IDENTIFIER_INDEX; i < tokens.size(); i++) {
                String token = tokens.get(i);

                // assume the token is an identifier
                Element element = context.getElement(token);
                Relationship relationship = context.getRelationship(token);
                if (element == null && relationship == null) {
                    throw new RuntimeException("The element/relationship \"" + token + "\" does not exist");
                }

                if (element != null) {
                    if (element instanceof CustomElement) {
                        view.remove((CustomElement) element);
                    } else if (element instanceof Person) {
                        view.remove((Person) element);
                    } else if (element instanceof SoftwareSystem) {
                        view.remove((SoftwareSystem) element);
                    } else if (element instanceof Container && (view instanceof ContainerView)) {
                        ((ContainerView) view).remove((Container) element);
                    } else if (element instanceof Container && (view instanceof ComponentView)) {
                        ((ComponentView) view).remove((Container) element);
                    } else if (element instanceof Component && (view instanceof ComponentView)) {
                        ((ComponentView) view).remove((Component) element);
                    } else {
                        throw new RuntimeException("The element \"" + token + "\" can not be added to this view");
                    }
                }

                if (relationship != null) {
                    view.remove(relationship);
                }
            }
        }
    }

}