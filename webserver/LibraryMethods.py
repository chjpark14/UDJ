"""
Copyright 2011 Kurtis L. Nusbaum

This file is part of UDJ.

UDJ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

UDJ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with UDJ.  If not, see <http://www.gnu.org/licenses/>.
"""
import json
import web
import MahData

def getJSONObject(dbrow):
  return LibraryEntry(
    dbrow.id, 
    dbrow.hostId, 
    dbrow.song, 
    dbrow.artist, 
    dbrow.album, 
    dbrow.isDeleted)


def getJSONArrayFromResults(results):
  toReturn = []
  for result in results:
    toReturn.append(getJSONObject(result))
  return toReturn
       

def addSongToLibrary(to_add, db):
  idreturn = db.insert('library', 
    song=to_add['song'],
    artist=to_add['artist'],
    album=to_add['album'],
    hostId=to_add['host_lib_song_id'])
  toReturn = db.select(
    'library', 
    where=web.db.sqlwhere({'id' : idreturn})
  )
  row = toReturn[0]
  return LibraryEntry(row.id, row.hostId, row.song, row.artist, row.album, row.isDeleted)


def addToLibrary(added, db):
  toReturn = []
  for toAdd in added:
    toReturn.append(addSongToLibrary(toAdd, db))
  return toReturn
  
  

class LibraryEntry:
  INVALID_SERVER_LIB_ID = -1
  INVALID_HOST_LIB_ID = -1
  DEFAULT_SONG_NAME = ''
  DEFAULT_ARTIST_NAME = ''
  DEFAULT_ALBUM_NAME= ''
  DEFAULT_DELETED_STATUS = False

  SERVER_ID_PARAM = "server_lib_song_id"
  HOST_ID_PARAM = "host_lib_song_id"
  SONG_PARAM = "song"
  ARTIST_PARAM = "artist"
  ALBUM_PARAM = "album"
  IS_DELETED_PARAM = "is_deleted"


  def __init__(
    self, 
    server_id=INVALID_SERVER_LIB_ID, 
    host_id=INVALID_HOST_LIB_ID,
    song=DEFAULT_SONG_NAME,
    artist=DEFAULT_ARTIST_NAME,
    album=DEFAULT_ALBUM_NAME,
    deleteStatus=DEFAULT_DELETED_STATUS
  ):
    self._server_id = server_id
    self._host_id = host_id
    self._song = song
    self._artist = artist
    self._album = album
    self._deleteStatus = deleteStatus

  def getServerId(self):
    return self._server_id

  def getHostId(self):
    return self._host_id

  def getSong(self):
    return self._song

  def getArtist(self):
    return self._artist

  def getAlbum(self):
    return self._album

  def getDeleteStatus(self):
    return self._deleteStatus
  
  def getDeleteStatusString(self):
    if(self._deleteStatus):
      return "true"
    else:
      return "false"
    
class LibraryJSONEncoder(json.JSONEncoder):
  def default(self, obj):
    if isinstance(obj, LibraryEntry):
      return {
        LibraryEntry.SERVER_ID_PARAM : obj.getServerId(),
        LibraryEntry.HOST_ID_PARAM : obj.getHostId(),
        LibraryEntry.SONG_PARAM : obj.getSong(),
        LibraryEntry.ARTIST_PARAM : obj.getArtist(),
        LibraryEntry.ALBUM_PARAM : obj.getAlbum(),
        LibraryEntry.IS_DELETED_PARAM : obj.getDeleteStatusString(),
      }
    else:
      return json.JSONEncoder.default(self, obj)

class LibrarySearch:
  def GET(self):
    data = web.input()
    db = MahData.getDBConnection()
    results = db.select('library')
    parray = getJSONArrayFromResults(results)
    web.header('Content-Type', 'application/json')
    return json.dumps(parray, cls=LibraryJSONEncoder)

class Library:
  def POST(self):
    db = MahData.getDBConnection()
    songs = json.loads(web.input().to_add)
    addedSongs = addToLibrary(songs,db)
    web.header('Content-Type', 'application/json')
    return json.dumps(addedSongs, cls=LibraryJSONEncoder)
    
class LibraryRandom:
  def GET(self):
    #TODO actually implement this
    return None

