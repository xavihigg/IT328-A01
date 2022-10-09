#!/bin/bash
clear

chmod 755 $HOME

if [ ! $# -eq 1 ] ; then 
	echo " You should provide a secret name. For example, if peekapoo is the secret name:"
        echo " bash sumbit328.sh peekapoo"
	exit 
fi 

if [ ! -d $HOME/IT328 ] ; then 
	echo " You haven't done your work or put your work in a wrong directory."
	exit 
fi 

chmod 755 $HOME
chmod -R 700 $HOME/IT328

secret=$HOME/Public/IT328myWork/$1

if [ ! -d $HOME/Public ] ; then
	echo "You didn't create $HOME/public before"
	mkdir $HOME/Public	
fi

chmod 755 $HOME/Public
chmod go+x $HOME

if [ ! -d $HOME/Public/IT328myWork ] ; then
	echo "You didn't create $HOME/public/myIT328myWork before"
	mkdir $HOME/Public/IT328myWork 	
fi
chmod 711 $HOME/Public/IT328myWork 

if [ ! -d $secret ] ; then
	mkdir $secret	 
fi
chmod 777 $secret

cp -r $HOME/IT328/* $secret
chmod -R 777 $secret

echo "Done!!"
