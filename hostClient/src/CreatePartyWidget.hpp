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
#ifndef CREATE_PARTY_WIDGET_HPP
#define CREATE_PARTY_WIDGET_HPP
#include <QWidget>

class QLineEdit;
class QPushButton;
class QLabel;

namespace UDJ{

class MusicLibrary;

class CreatePartyWidget : public QWidget{
Q_OBJECT
public:
  CreatePartyWidget(MusicLibrary *musicLibrary, QWidget *parent=0);
private:
  void setupUi();
  QLineEdit *nameEdit;
  QLineEdit *passwordEdit;
  QLineEdit *locationEdit;
  QLabel *createLabel;
  QPushButton *createPartyButton;
  MusicLibrary *musicLibrary;
};

}//end namspace UDJ


#endif //CREATE_PARTY_WIDGET_HPP
