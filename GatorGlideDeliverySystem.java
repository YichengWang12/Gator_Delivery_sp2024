import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Order {
    int orderId;
    int currentSystemTime;
    int orderValue;
    int deliveryTime;
    double priority;
    int eta;

    public Order(int orderId, int currentSystemTime, int orderValue, int deliveryTime) {
        this.orderId = orderId;
        this.currentSystemTime = currentSystemTime;
        this.orderValue = orderValue;
        this.deliveryTime = deliveryTime;
        this.priority = calculatePriority(orderValue, currentSystemTime);
        this.eta = 0;
    }

    private double calculatePriority(int orderValue, int currentSystemTime) {
        double normalizedOrderValue = (double) orderValue / 50;
        double valueWeight = 0.3;
        double timeWeight = 0.7;
        return valueWeight * normalizedOrderValue - timeWeight * currentSystemTime;
    }
}


class AVLNode<T> {
    T data;
    int height;
    AVLNode<T> left;
    AVLNode<T> right;

    public AVLNode(T data) {
        this.data = data;
        this.height = 0;
        this.left = null;
        this.right = null;
    }
}

class AVLTreeManager<T> {
    private AVLNode<T> root;
    private Comparator<T> comparator;


    public AVLTreeManager(Comparator<T> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    public void insertOrder(T data) {
        root = insert(root, data);
    }

    public void deleteOrder(T data) {
        root = delete(root, data);
    }

    private AVLNode<T> insert(AVLNode<T> node, T data){
        if (node == null) {
            return new AVLNode<T>(data);
        }

        if (comparator.compare(data, node.data) < 0) {
            node.left = insert(node.left, data);
        } else {
            node.right = insert(node.right, data);
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);

        if (balance > 1 && comparator.compare(data, node.left.data) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && comparator.compare(data, node.right.data) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && comparator.compare(data, node.left.data) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && comparator.compare(data, node.right.data) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode<T> delete(AVLNode<T> node, T data) {
        if (node == null) {
            return null;
        }

        if (comparator.compare(data, node.data) < 0) {
            node.left = delete(node.left, data);
        } else if (comparator.compare(data, node.data) > 0) {
            node.right = delete(node.right, data);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left == null) ? node.right : node.left;
            } else {
                AVLNode<T> successor = findMin(node.right);
                node.data = successor.data;
                node.right = delete(node.right, successor.data);
            }
        }

        if (node == null) {
            return null;
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode<T> findMin(AVLNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private int getHeight(AVLNode<T> node) {
        return (node == null) ? -1 : node.height;
    }

    private int getBalance(AVLNode<T> node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    private AVLNode<T> leftRotate(AVLNode<T> node) {
        AVLNode<T> rightChild = node.right;
        AVLNode<T> leftGrandChild = rightChild.left;

        rightChild.left = node;
        node.right = leftGrandChild;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        rightChild.height = 1 + Math.max(getHeight(rightChild.left), getHeight(rightChild.right));

        return rightChild;
    }

    private AVLNode<T> rightRotate(AVLNode<T> node) {
        AVLNode<T> leftChild = node.left;
        AVLNode<T> rightGrandChild = leftChild.right;

        leftChild.right = node;
        node.left = rightGrandChild;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        leftChild.height = 1 + Math.max(getHeight(leftChild.left), getHeight(leftChild.right));

        return leftChild;
    }

    //1. print(orderId): Prints the order details of the given order_id.
    //Output format: [orderId, currentSystemTime, orderValue, deliveryTime, ETA]
    public void search(int orderId) {
        AVLNode<T> current = root;
        while (current != null) {
            if (((Order) current.data).orderId == orderId) {
                Order order = (Order) current.data;
                System.out.println("[" + order.orderId + ", " + order.currentSystemTime + ", " + order.orderValue + ", " + order.deliveryTime + ", " + order.eta + "]");
                return;
            } else if (((Order) current.data).orderId < orderId) {
                current = current.right;
            } else {
                current = current.left;
            }
        }
        System.out.println("Order not found");
    }

    //2. print(time1, time2): Prints all the orders that will be delivered within the given times (including both
    //times) and are undelivered.
    //Output format: if orders exist [orderId1, orderId2, ………], There are no orders in that time period if none
    public void search(int time1, int time2){
        List<Integer> orderIds = new ArrayList<>();
        inOrderSearch(root, time1, time2, orderIds);
        if(orderIds.isEmpty()) {
            System.out.println("There are no orders in that time period");
        } else {
            System.out.println(orderIds);
        }
    }

    public void inOrderSearch(AVLNode<T> node, int time1, int time2, List<Integer> orderIds) {
        if (node == null) {
            return;
        }
        inOrderSearch(node.left, time1, time2, orderIds);
        if (((Order) node.data).eta >= time1 && ((Order) node.data).eta <= time2) {
            orderIds.add(((Order) node.data).orderId);
        }
        inOrderSearch(node.right, time1, time2, orderIds);
    }

    public int inOrderSearchForRank(AVLNode<T> node, int orderId) {
        if(node == null){
            return 0;
        }
        int left = inOrderSearchForRank(node.left, orderId);
        if(((Order) node.data).orderId == orderId){
            return left ;
        }
        int right = inOrderSearchForRank(node.right, orderId);

        return left + right + 1;

    }

    //getRankOfOrder(orderId): Takes the order_id and returns how many orders will be delivered before
    //it.
    //Output format: Order {orderId} will be delivered after {numberOfOrders} order
    public void computeRank(int orderId){
        int pre = inOrderSearchForRank(root, orderId);
        System.out.println("Order " + orderId + " will be delivered after " + pre + " order");
    }

    public T findNodeWithHigherPriority(T target) {
        AVLNode<T> node = root;
        AVLNode<T> successor = null;

        while (node != null) {
            if (comparator.compare(node.data, target) > 0) {
                successor = node;
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return (successor == null) ? null : successor.data;
    }
}

public class GatorGlideDeliverySystem {
    private AVLTreeManager<Order> priorityTreeManager;
    private AVLTreeManager<Order> etaTreeManager;

    public GatorGlideDeliverySystem() {
        this.priorityTreeManager = new AVLTreeManager<>((order1, order2) -> Double.compare(order1.priority, order2.priority));
        this.etaTreeManager = new AVLTreeManager<>((order1, order2) -> order1.eta - order2.eta);
    }

    // 使用AVLTreeManager来管理订单
    public void addNewOrder(Order order) {
        priorityTreeManager.insertOrder(order);
        // 如果需要的话，也可以在etaTreeManager中插入
        etaTreeManager.insertOrder(order);
    }

    public void removeOrder(Order order) {
        priorityTreeManager.deleteOrder(order);
        // 如果需要的话，也可以在etaTreeManager中删除
        etaTreeManager.deleteOrder(order);
    }

    public void print(int orderId) {
        priorityTreeManager.search(orderId);
    }

    public void print(int time1, int time2) {
        etaTreeManager.search(time1, time2);
    }

    public void getRankOfOrder(int orderId) {
        etaTreeManager.computeRank(orderId);
    }

    //4. createOrder(order_id, current_system_time, orderValue, deliveryTime): Creates the order, prints
    //the ETA, and also prints which previously unfulfilled orders have been delivered along with their delivery
    //times.
    //Output format: Order {orderId} has been created - ETA: {ETA}
    //Order {orderId} has been delivered at time {ETA} for all such orders if they exist, each in a new line.
    //*Note that the deliveryTime is the time that is needed for the order to be delivered by the delivery agent,
    //the time taken to get back from the delivery destination back to the source also needs to be accounted,
    //which is the same as the deliveryTime.
    public void createOrder(int order_id, int current_system_time, int orderValue, int deliveryTime){
        Order newOrder = new Order(order_id, current_system_time, orderValue, deliveryTime);
        priorityTreeManager.insertOrder(newOrder);
        Order successor = (Order) priorityTreeManager.findNodeWithHigherPriority(newOrder);
        if(successor != null){
            newOrder.eta = successor.eta + deliveryTime;
        } else {
            newOrder.eta = current_system_time + deliveryTime;
        }
        etaTreeManager.insertOrder(newOrder);
        System.out.println("Order " + order_id + " has been created - ETA: " + newOrder.eta);
    }

    // ... 其他方法 ...
}


