/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Daman
 */
public class KDTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Point> points = new ArrayList<Point>();
//        Random rand = new Random(System.currentTimeMillis());
//        for (int i = 0; i < 10; i++) {
//            double x = rand.nextInt(10);
//            double y = rand.nextInt(10);
//            double z = rand.nextInt(10);
//            Point point = new Point(x, y, z);
//            points.add(point);
//        }
        points.add(new Point(7, 2, 1));
        points.add(new Point(8, 7, 8));
        points.add(new Point(2, 5, 7));
        points.add(new Point(1, 4, 9));
        points.add(new Point(4, 8, 9));
        points.add(new Point(2, 8, 2));
        points.add(new Point(5, 0, 8));
        points.add(new Point(8, 9, 0));
        points.add(new Point(7, 6, 6));
        points.add(new Point(8, 2, 3));
        Node root = Node.kdtree(points, 0);
        System.out.println("Done");
        traverse(root);
        System.out.println(getString(root, "", true));
        Node toInsert = new Node(new Point(9, 2, 0));
        root = Node.insert(toInsert, root, 0);
        traverse(root);
        System.out.println(getString(root, "", true));
        Collection t = Node.nearestNeighbourSearch(root, 3);
        System.out.println("Done");
    }

    static class Node implements Comparable<Node> {

        Node leftChild;
        Node rightChild;
        Point location;
        Node parent;
        int depth;

        Node() {

        }

        Node(Node leftChild, Node rightChild, Point location, Node parent, int depth) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.location = location;
            this.parent = parent;
            this.depth = depth;
        }

        Node(Point location) {
            this.location = location;
        }

        public static int compareX(Node a, Node b) {
            if (a.location.x > b.location.x) {
                return 1;
            } else if (a.location.x < b.location.x) {
                return -1;
            } else {
                return 0;
            }
        }

        public static int compareY(Node a, Node b) {
            if (a.location.y > b.location.y) {
                return 1;
            } else if (a.location.y < b.location.y) {
                return -1;
            } else {
                return 0;
            }
        }

        public static int compareZ(Node a, Node b) {
            if (a.location.z > b.location.z) {
                return 1;
            } else if (a.location.z < b.location.z) {
                return -1;
            } else {
                return 0;
            }
        }

        public double manhattanDistance(Node a) {
            return Math.abs(a.location.x - this.location.x) + Math.abs(a.location.y - this.location.y) + Math.abs(a.location.z - this.location.z);
        }

        public double euclideanDistance(Node a) {
            double dx = 0.0, dy = 0.0, dz = 0.0;
            dx = a.location.x - this.location.x;
            dy = a.location.y - this.location.y;
            dz = a.location.z - this.location.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public static Node insert(Node newNode, Node parent, int depth) {
            int axis = depth % 3;
            if (parent == null) {
                newNode.parent = parent;
                newNode.depth = depth;
                return newNode;
            }
            int result;
            if (axis == 0) {
                result = compareX(newNode, parent);
                if (result > 0) {
                    parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
                } else if (result < 0) {
                    parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
                }
            } else if (axis == 1) {
                result = compareY(newNode, parent);
                if (result > 0) {
                    parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
                } else if (result < 0) {
                    parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
                }
            } else if (axis == 2) {
                result = compareZ(newNode, parent);
                if (result > 0) {
                    parent.rightChild = insert(newNode, parent.rightChild, depth + 1);
                } else if (result < 0) {
                    parent.leftChild = insert(newNode, parent.leftChild, depth + 1);
                }
            }
            return parent;
        }

        public static Node kdtree(ArrayList<Point> points, int depth) {
            if (points == null || points.size() == 0) {
                return null;
            }
            int axis = depth % 3;
            switch (axis) {
                case 0:
                    Collections.sort(points, Point.X_COMPARATOR);
                    break;
                case 1:
                    Collections.sort(points, Point.Y_COMPARATOR);
                    break;
                case 2:
                    Collections.sort(points, Point.Z_COMPARATOR);
                    break;
                default:
                    break;
            }
            Node node = new Node();
            if (points.size() > 0) {
                int medianIndex = points.size() / 2;
                Point currentPoint = points.get(medianIndex);
                node.location = currentPoint;
                node.depth = depth;
                ArrayList<Point> less = new ArrayList<Point>(points.size() - 1);
                ArrayList<Point> more = new ArrayList<Point>(points.size() - 1);
                //Process list to see where each non-median point lies
                for (int i = 0; i < points.size(); i++) {
                    if (i == medianIndex) {
                        continue;
                    }
                    Point p = points.get(i);
                    if (currentPoint.compareTo(p, axis) <= 0) {
                        less.add(p);
                    } else {
                        more.add(p);
                    }
                }
                if (less.size() > 0) {
                    node.leftChild = kdtree(less, depth + 1);
                    node.leftChild.parent = node;
                }
                if (more.size() > 0) {
                    node.rightChild = kdtree(more, depth + 1);
                    node.rightChild.parent = node;
                }
            }
            return node;
        }

        public boolean equals(Node a) {
            if (this.location.x == a.location.x && this.location.y == a.location.y && this.location.z == a.location.z) {
                return true;
            } else {
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        public static Collection nearestNeighbourSearch(Node root, int K) {
            if (root == null) {
                return Collections.EMPTY_LIST;
            }

            // Map used for results
            TreeSet<Node> results = new TreeSet<Node>();

            // Find the closest leaf node
            Node prev = null;
            Node node = root;
            while (node != null) {
                if (compareTo(node.depth, 3, root.location, node.location) <= 0) {
                    // Lesser
                    prev = node;
                    node = node.leftChild;
                } else {
                    // Greater
                    prev = node;
                    node = node.rightChild;
                }
            }
            Node leaf = prev;

            if (leaf != null) {
                // Used to not re-examine nodes
                Set<Node> examined = new HashSet<Node>();

                // Go up the tree, looking for better solutions
                node = leaf;
                while (node != null) {
                    // Search node
                    searchNode(root, node, K, results, examined);
                    node = node.parent;
                }
            }

            // Load up the collection of the results
            Collection<Point> collection = new ArrayList<Point>(K);
            for (Node nodes : results) {
                collection.add(nodes.location);
            }
            return collection;
        }

        private static final void searchNode(Node root, Node node, int K, TreeSet<Node> results, Set<Node> examined) {
            examined.add(node);

            // Search node
            Node lastNode = null;
            Double lastDistance = Double.MAX_VALUE;
            if (results.size() > 0) {
                lastNode = results.last();
                lastDistance = lastNode.manhattanDistance(root);
            }
            Double nodeDistance = node.manhattanDistance(root);
            if (nodeDistance.compareTo(lastDistance) < 0) {
                if (results.size() == K && lastNode != null) {
                    results.remove(lastNode);
                }
                results.add(node);
            } else if (nodeDistance.equals(lastDistance)) {
                results.add(node);
            } else if (results.size() < K) {
                results.add(node);
            }
            lastNode = results.last();
            lastDistance = lastNode.manhattanDistance(root);

            int axis = node.depth % 3;
            Node lesser = node.leftChild;
            Node greater = node.rightChild;

            // Search children branches, if axis aligned distance is less than
            // current distance
            if (lesser != null && !examined.contains(lesser)) {
                examined.add(lesser);

                double nodePoint = Double.MIN_VALUE;
                double valuePlusDistance = Double.MIN_VALUE;
                if (axis == 0) {
                    nodePoint = node.location.x;
                    valuePlusDistance = root.location.x - lastDistance;
                } else if (axis == 1) {
                    nodePoint = node.location.y;
                    valuePlusDistance = root.location.y - lastDistance;
                } else {
                    nodePoint = node.location.z;
                    valuePlusDistance = root.location.z - lastDistance;
                }
                boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

                // Continue down lesser branch
                if (lineIntersectsCube) {
                    searchNode(root, lesser, K, results, examined);
                }
            }
            if (greater != null && !examined.contains(greater)) {
                examined.add(greater);

                double nodePoint = Double.MIN_VALUE;
                double valuePlusDistance = Double.MIN_VALUE;
                if (axis == 0) {
                    nodePoint = node.location.x;
                    valuePlusDistance = root.location.x + lastDistance;
                } else if (axis == 1) {
                    nodePoint = node.location.y;
                    valuePlusDistance = root.location.y + lastDistance;
                } else {
                    nodePoint = node.location.z;
                    valuePlusDistance = root.location.z + lastDistance;
                }
                boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

                // Continue down greater branch
                if (lineIntersectsCube) {
                    searchNode(root, greater, K, results, examined);
                }
            }
        }

        public static int compareTo(int depth, int dim, Point aLocation, Point bLocation) {
            int axis = depth % dim;
            if (axis == 0) {
                return Point.X_COMPARATOR.compare(aLocation, bLocation);
            }
            if (axis == 1) {
                return Point.Y_COMPARATOR.compare(aLocation, bLocation);
            }
            return Point.Z_COMPARATOR.compare(aLocation, bLocation);
        }

        @Override
        public int compareTo(Node o) {
            return compareTo(depth, 3, this.location, o.location);
        }
    }

    public static void traverse(Node root) { // Each child of a tree is a root of its subtree.
        if (root.leftChild != null) {
            traverse(root.leftChild);
        }
        System.out.println(root.location.toString());
        if (root.rightChild != null) {
            traverse(root.rightChild);
        }
    }

    private static String getString(Node node, String prefix, boolean isTail) {
        StringBuilder builder = new StringBuilder();

        if (node.parent != null) {
            String side = "left";
            if (node.parent.rightChild != null && node.location.equals(node.parent.rightChild.location)) {
                side = "right";
            }
            builder.append(prefix + (isTail ? "└── " : "├── ") + "[" + side + "] " + "depth=" + node.depth + " id="
                    + node.location + "\n");
        } else {
            builder.append(prefix + (isTail ? "└── " : "├── ") + "depth=" + node.depth + " id=" + node.location + "\n");
        }
        List<Node> children = null;
        if (node.leftChild != null || node.rightChild != null) {
            children = new ArrayList<Node>(2);
            if (node.leftChild != null) {
                children.add(node.leftChild);
            }
            if (node.rightChild != null) {
                children.add(node.rightChild);
            }
        }
        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                builder.append(getString(children.get(i), prefix + (isTail ? "    " : "│   "), false));
            }
            if (children.size() >= 1) {
                builder.append(getString(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "),
                        true));
            }
        }

        return builder.toString();
    }
}
