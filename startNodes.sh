DIR="$( cd "$( dirname "$0" )" &&pwd )"
JAR_PATH="$DIR/conf/:$DIR/build/libs/P2P-Network-1.0-SNAPSHOT.jar"
MACHINE_LIST="$DIR/conf/machine_list"
SCRIPT="java -cp $JAR_PATH cs455.overlay.node.MessagingNode 129.82.44.131 50000"
COMMAND='gnome-terminal --geometry=200x40'
for machine in `cat $MACHINE_LIST`
do
    OPTION='--tab -t "'$machine'" -e "ssh -t '$machine' cd '$DIR'; echo '$SCRIPT'; '$SCRIPT'"'
    COMMAND+=" $OPTION"
done
eval $COMMAND &