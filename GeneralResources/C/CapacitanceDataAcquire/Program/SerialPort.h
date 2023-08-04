/**
 * @author Pedro Rojo (pedroeroca@outlook.com) - Lord448@github.com
 * @note  This Source Code Form is subject to the terms of the Mozilla Public
  		  License, v. 2.0. If a copy of the MPL was not distributed with this
  		  file, You can obtain one at http://mozilla.org/MPL/2.0/.
   @brief  
 * @version 0.1.0
 * @date 2023-06-02
 * @copyright Copyright (c) 2023
 */

#ifndef SerialPort_H_
#define SerialPort_H_

#include <errno.h>
#include <fcntl.h> 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>

int set_interface_attribs(int fd, int speed);

void set_mincount(int fd, int mcount);

#endif
