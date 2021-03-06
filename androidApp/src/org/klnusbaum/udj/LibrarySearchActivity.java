/**
 * Copyright 2011 Kurtis L. Nusbaum
 * 
 * This file is part of UDJ.
 * 
 * UDJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * UDJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UDJ.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.klnusbaum.udj;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.Context;
import android.view.LayoutInflater;
import android.util.Log;
import android.accounts.Account;
import android.app.SearchManager;

import java.util.List;

import org.klnusbaum.udj.containers.LibraryEntry;
import org.klnusbaum.udj.network.PlaylistSyncService;


/**
 * An Activity which displays the results of a library search.
 */
public class LibrarySearchActivity extends FragmentActivity{

  public static final String SEARCH_QUERY_EXTRA = "search_query";
  private static final int LIB_SEARCH_LOADER_TAG = 0;
  private String searchQuery;

  
  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    searchQuery = null; 
    if(savedInstanceState != null){
      searchQuery = savedInstanceState.getString(SEARCH_QUERY_EXTRA);
    }
    else{
      searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
    }

    if(searchQuery == null){
      //TODO throw some sort of error.
    }

    //TODO before calling fragment, to get ID and give that to it.
    FragmentManager fm = getSupportFragmentManager();
    if(fm.findFragmentById(android.R.id.content) == null){
      Bundle queryBundle = new Bundle();
      queryBundle.putString(SEARCH_QUERY_EXTRA, searchQuery);
      LibrarySearchFragment list = new LibrarySearchFragment();
      list.setArguments(queryBundle);
      fm.beginTransaction().add(android.R.id.content, list).commit();
    }
  }

  public static class LibrarySearchFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<List<LibraryEntry>>
  {
    /** Adapter used to help display the contents of the library. */
    LibrarySearchAdapter searchAdapter;
    private String searchQuery;
  
    private View.OnClickListener addSongToPlaylistListener =
      new View.OnClickListener(){
        public void onClick(View v){
          LibraryEntry songToAdd = 
            (LibraryEntry)v.getTag(R.id.LIB_ENTRY_VIEW_TAG);
          Intent addSongIntent = new Intent(
            Intent.ACTION_INSERT,
            UDJPartyProvider.PLAYLIST_URI,
            getActivity(),
            PlaylistSyncService.class);
          addSongIntent.putExtra(
            PlaylistSyncService.LIB_ENTRY_EXTRA,
            LibraryEntry.toBundle(songToAdd)
          );
          getActivity().startService(addSongIntent);
        }
      };

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
      super.onActivityCreated(savedInstanceState);
      searchQuery = null; 
      if(savedInstanceState != null){
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_EXTRA);
      }
      else{
        Bundle args = getArguments();
        if(args != null){
          searchQuery = args.getString(SEARCH_QUERY_EXTRA);
        }
      }

      setEmptyText(getActivity().getString(R.string.no_library_songs));
      //setHasOptionsMenu(true);

      searchAdapter = new LibrarySearchAdapter(getActivity());
      setListAdapter(searchAdapter);
      setListShown(false);
      Bundle loaderArgs = new Bundle();
      loaderArgs.putString(SEARCH_QUERY_EXTRA, searchQuery);
      getLoaderManager().initLoader(LIB_SEARCH_LOADER_TAG, loaderArgs, this);
    }

    public Loader<List<LibraryEntry>> onCreateLoader(int id, Bundle args){
      if(id == LIB_SEARCH_LOADER_TAG){
        String query = args.getString(SEARCH_QUERY_EXTRA);
        return new LibrarySearchLoader(getActivity(), query);
      }
      return null;
    }

    public void onLoadFinished(
      Loader<List<LibraryEntry>> loader,
      List<LibraryEntry> data)
    {
      if(data != null){
        Log.i("TAG", "Size of data: " + data.size());
      }
      else{
        Log.i("TAG", "Data returned was null");
      }
      searchAdapter = new LibrarySearchAdapter(
        getActivity(), 
        data,
        addSongToPlaylistListener);
      setListAdapter(searchAdapter);
      if(isResumed()){
        setListShown(true);
      }
      else if(isVisible()){
        setListShownNoAnimation(true);
      }
    }

    public void onLoaderReset(Loader<List<LibraryEntry>> loader){
      searchAdapter = new LibrarySearchAdapter(getActivity());
    }
  }
}
