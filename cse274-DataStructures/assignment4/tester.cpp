/*
 * tester.cpp
 *
 *  Created on: Oct 28, 2014
 *      Author: Nick
 */

#include "BinaryTree.h"
#include "BinarySearchTree.h"

using namespace std;


class BSTTester {
protected:
	static ods::BinarySearchTree<ods::BSTNode1<int>, int> balancedTree (bool out){
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree;
		tree.add(50);
		tree.add(45);
		tree.add(60);
		tree.add(40);
		tree.add(47);
		tree.add(55);
		tree.add(70);
		if(out)
			cout << "Creating BST: 50, 45, 60, 40, 47, 55, 70" << endl;
		return tree;
	};
	static ods::BinarySearchTree<ods::BSTNode1<int>, int> unbalancedTree1 (){
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree = balancedTree(false);
		tree.remove(70);
		tree.remove(60);
		cout << "Creating BST: 50, 45, 40, 47, 55" << endl;
		return tree;
	};
	static ods::BinarySearchTree<ods::BSTNode1<int>, int> unbalancedTree2 (){
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree = balancedTree(false);
		tree.remove(45);
		tree.remove(47);
		tree.remove(40);
		cout << "Creating BST: 50, 60, 55, 70" << endl;
		return tree;
	};
public:
	static void _height2() {
		cout << "Testing height2(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree2 = unbalancedTree1();
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree3 = unbalancedTree2();
		cout << "Tree 1 height (expecting 3): " << tree1.height() << endl;
		cout << "Tree 2 height (expecting 3): " << tree2.height() << endl;
		cout << "Tree 3 height (expecting 3): " << tree3.height() << endl << endl;
	};
	static void _isBalanced() {
		cout << "Testing isBalanced(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree2 = unbalancedTree1();
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree3 = unbalancedTree2();
		cout << "Tree 1 balance (expecting 3): " << tree1.isBalanced() << endl;
		cout << "Tree 2 balance (expecting 0): " << tree2.isBalanced() << endl;
		cout << "Tree 3 balance (expecting 0): " << tree3.isBalanced() << endl << endl;

	};
	static void _preOrderNumber() {
		cout << "Testing preOrderNumber(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		tree1.preOrderNumber();
		cout << "Getting pre-order number of 70 (expecting 7): " << tree1.getNode(70)->_pre_order << endl;
		cout << "Getting pre-order number of 45 (expecting 2): " << tree1.getNode(45)->_pre_order << endl << endl;
	};
	static void _inOrderNumber() {
		cout << "Testing inOrderNumber(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		tree1.inOrderNumber();
		cout << "Getting in-order number of 70 (expecting 7): " << tree1.getNode(70)->_in_order << endl;
		cout << "Getting in-order number of 45 (expecting 2): " << tree1.getNode(45)->_in_order << endl << endl;
	};
	static void _postOrderNumber() {
		cout << "Testing postOrderNumber(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		tree1.postOrderNumber();
		cout << "Getting post-order number of 70 (expecting 3): " << tree1.getNode(70)->_post_order << endl;
		cout << "Getting post-order number of 45 (expecting 5): " << tree1.getNode(45)->_post_order << endl << endl;
	};
	static void _getNode() {
		cout << "Testing getNode(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		cout << "Getting node of 70 (expecting 70): " << tree1.getNode(70)->x << endl;
		cout << "Getting node of 45: (expecting 45)" << tree1.getNode(45)->x << endl << endl;

	};
	static void _getLE() {
		cout << "Testing getLE(): " << endl;
		ods::BinarySearchTree<ods::BSTNode1<int>, int> tree1 = balancedTree(true);
		ods::DLList<int> less70 = tree1.getLE(70);
		cout << "Getting values less than 70: ";
		for(int i = 0; i < less70.size(); i++)
			cout << less70.get(i) << " ";
		cout << endl;
		ods::DLList<int> less45 = tree1.getLE(45);
		cout << "Getting values less than 45: ";
		for(int i = 0; i < less45.size(); i++)
			cout << less45.get(i) << " ";
		cout << endl << endl;
	};
};


int main() {
	BSTTester::_height2();
	BSTTester::_isBalanced();
	BSTTester::_preOrderNumber();
	BSTTester::_inOrderNumber();
	BSTTester::_postOrderNumber();
	BSTTester::_getNode();
	BSTTester::_getLE();
	return 0;
}
