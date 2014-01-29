/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.akiban.sql.types;

import com.akiban.sql.StandardException;

/** Character set and collation for character types. */
public final class CharacterTypeAttributes
{
    public static enum CollationDerivation {
        NONE, IMPLICIT, EXPLICIT
    }

    private String characterSet;
    private String collation;
    private CollationDerivation collationDerivation;

    public CharacterTypeAttributes(String characterSet,
                                   String collation, 
                                   CollationDerivation collationDerivation) {
        this.characterSet = characterSet;
        this.collation = collation;
        this.collationDerivation = collationDerivation;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public CollationDerivation getCollationDerivation() {
        return collationDerivation;
    }

    public static CharacterTypeAttributes forCharacterSet(String characterSet) {
        return new CharacterTypeAttributes(characterSet, null, null);
    }

    public static CharacterTypeAttributes forCollation(CharacterTypeAttributes base,
                                                       String collation) {
        String characterSet = null;
        if (base != null)
            characterSet = base.characterSet;
        return new CharacterTypeAttributes(characterSet, 
                                           collation, CollationDerivation.EXPLICIT);
    }

    public static CharacterTypeAttributes mergeCollations(CharacterTypeAttributes ta1,
                                                          CharacterTypeAttributes ta2)
            throws StandardException {
        if ((ta1 == null) || (ta1.collationDerivation == null)) {
            return ta2;
        }
        else if ((ta2 == null) || (ta2.collationDerivation == null)) {
            return ta1;
        }
        else if (ta1.collationDerivation == CollationDerivation.EXPLICIT) {
            if (ta2.collationDerivation == CollationDerivation.EXPLICIT) {
                if (!ta1.collation.equals(ta2.collation))
                    throw new StandardException("Incompatible collations: " +
                                                ta1 + " " + ta1.collation + " and " +
                                                ta2 + " " + ta2.collation);
            }
            return ta1;
        }
        else if (ta2.collationDerivation == CollationDerivation.EXPLICIT) {
            return ta2;
        }
        else if ((ta1.collationDerivation == CollationDerivation.IMPLICIT) &&
                 (ta2.collationDerivation == CollationDerivation.IMPLICIT) &&
                 ta1.collation.equals(ta2.collation)) {
            return ta1;
        }
        else {
            return new CharacterTypeAttributes(null, null, CollationDerivation.NONE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CharacterTypeAttributes)) return false;
        CharacterTypeAttributes other = (CharacterTypeAttributes)o;
        return (((characterSet == null) ?
                 (other.characterSet == null) :
                 characterSet.equals(other.characterSet)) &&
                ((collation == null) ?
                 (other.collation == null) :
                 collation.equals(other.collation)));
    }

    @Override
    public String toString() {
        if ((characterSet == null) && (collation == null)) return "none";
        StringBuilder str = new StringBuilder();
        if (characterSet != null) {
            str.append("CHARACTER SET ");
            str.append(characterSet);
        }
        if (collation != null) {
            if (characterSet != null) str.append(" ");
            str.append("COLLATE ");
            str.append(collation);
        }
        return str.toString();
    }
    
}