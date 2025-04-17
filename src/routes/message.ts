import express from 'express';
import { Message } from '../models/Message';
import { auth } from '../middleware/auth';

const router = express.Router();

// Send a message
router.post('/', auth, async (req, res) => {
  try {
    const { receiver, content } = req.body;
    const message = new Message({
      sender: req.user._id,
      receiver,
      content
    });
    await message.save();
    res.status(201).json(message);
  } catch (error: any) {
    res.status(400).json({ error: error?.message || 'Failed to send message' });
  }
});

// Get all messages between two users
router.get('/:userId', auth, async (req, res) => {
  try {
    const messages = await Message.find({
      $or: [
        { sender: req.user._id, receiver: req.params.userId },
        { sender: req.params.userId, receiver: req.user._id }
      ]
    }).sort({ createdAt: 1 });
    res.json(messages);
  } catch (error: any) {
    res.status(400).json({ error: error?.message || 'Failed to get messages' });
  }
});

// Mark messages as read
router.patch('/read/:userId', auth, async (req, res) => {
  try {
    await Message.updateMany(
      {
        sender: req.params.userId,
        receiver: req.user._id,
        read: false
      },
      { read: true }
    );
    res.json({ message: 'Messages marked as read' });
  } catch (error: any) {
    res.status(400).json({ error: error?.message || 'Failed to mark messages as read' });
  }
});

export default router; 