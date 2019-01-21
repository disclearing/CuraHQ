///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2008, Robert D. Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.impl.sync;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// THIS IS AN IMPLEMENTATION CLASS. DO NOT USE DIRECTLY!  //
// Access to these methods should be through TCollections //
////////////////////////////////////////////////////////////


import gnu.trove.iterator.*; 
import gnu.trove.procedure.*;
import gnu.trove.set.*;
import gnu.trove.function.*;
import gnu.trove.map.*;
import gnu.trove.*;

import java.util.Map;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class TSynchronizedLongIntMap implements TLongIntMap, Serializable {
	private static final long serialVersionUID = 1978198479659022715L;

	private final TLongIntMap m;     // Backing Map
	final Object      mutex;	// Object on which to synchronize

	public TSynchronizedLongIntMap( TLongIntMap m ) {
		if ( m == null )
			throw new NullPointerException();
		this.m = m;
		mutex = this;
	}

	public TSynchronizedLongIntMap( TLongIntMap m, Object mutex ) {
		this.m = m;
		this.mutex = mutex;
	}

	public int size() {
		synchronized( mutex ) { return m.size(); }
	}
	public boolean isEmpty(){
		synchronized( mutex ) { return m.isEmpty(); }
	}
	public boolean containsKey( long key ) {
		synchronized( mutex ) { return m.containsKey( key ); }
	}
	public boolean containsValue( int value ){
		synchronized( mutex ) { return m.containsValue( value ); }
	}
	public int get( long key ) {
		synchronized( mutex ) { return m.get( key ); }
	}

	public int put( long key, int value ) {
		synchronized( mutex ) { return m.put( key, value ); }
	}
	public int remove( long key ) {
		synchronized( mutex ) { return m.remove( key ); }
	}
	public void putAll( Map<? extends Long, ? extends Integer> map ) {
		synchronized( mutex ) { m.putAll( map ); }
	}
	public void putAll( TLongIntMap map ) {
		synchronized( mutex ) { m.putAll( map ); }
	}
	public void clear() {
		synchronized( mutex ) { m.clear(); }
	}

	private transient TLongSet keySet = null;
	private transient TIntCollection values = null;

	public TLongSet keySet() {
		synchronized( mutex ) {
			if ( keySet == null )
				keySet = new TSynchronizedLongSet( m.keySet(), mutex );
			return keySet;
		}
	}
	public long[] keys() {
		synchronized( mutex ) { return m.keys(); }
	}
	public long[] keys( long[] array ) {
		synchronized( mutex ) { return m.keys( array ); }
	}

	public TIntCollection valueCollection() {
		synchronized( mutex ) {
			if ( values == null )
				values = new TSynchronizedIntCollection( m.valueCollection(), mutex );
			return values;
		}
	}
	public int[] values() {
		synchronized( mutex ) { return m.values(); }
	}
	public int[] values( int[] array ) {
		synchronized( mutex ) { return m.values( array ); }
	}

	public TLongIntIterator iterator() {
		return m.iterator(); // Must be manually synched by user!
	}

	// these are unchanging over the life of the map, no need to lock
	public long getNoEntryKey() { return m.getNoEntryKey(); }
	public int getNoEntryValue() { return m.getNoEntryValue(); }

	public int putIfAbsent( long key, int value ) {
		synchronized( mutex ) { return m.putIfAbsent( key, value ); }
	}
	public boolean forEachKey( TLongProcedure procedure ) {
		synchronized( mutex ) { return m.forEachKey( procedure ); }
	}
	public boolean forEachValue( TIntProcedure procedure ) {
		synchronized( mutex ) { return m.forEachValue( procedure ); }
	}
	public boolean forEachEntry( TLongIntProcedure procedure ) {
		synchronized( mutex ) { return m.forEachEntry( procedure ); }
	}
	public void transformValues( TIntFunction function ) {
		synchronized( mutex ) { m.transformValues( function ); }
	}
	public boolean retainEntries( TLongIntProcedure procedure ) {
		synchronized( mutex ) { return m.retainEntries( procedure ); }
	}
	public boolean increment( long key ) {
		synchronized( mutex ) { return m.increment( key ); }
	}
	public boolean adjustValue( long key, int amount ) {
		synchronized( mutex ) { return m.adjustValue( key, amount ); }
	}
	public int adjustOrPutValue( long key, int adjust_amount, int put_amount ) {
		synchronized( mutex ) { return m.adjustOrPutValue( key, adjust_amount, put_amount ); }
	}

	public boolean equals( Object o ) {
		synchronized( mutex ) { return m.equals( o ); }
	}
	public int hashCode() {
		synchronized( mutex ) { return m.hashCode(); }
	}
	public String toString() {
		synchronized( mutex ) { return m.toString(); }
	}
	private void writeObject( ObjectOutputStream s ) throws IOException {
		synchronized( mutex ) { s.defaultWriteObject(); }
	}
}
