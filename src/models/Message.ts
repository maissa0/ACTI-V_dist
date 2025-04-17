import mongoose from 'mongoose';

export interface IMessage extends mongoose.Document {
  sender: mongoose.Types.ObjectId;
  receiver: mongoose.Types.ObjectId;
  content: string;
  read: boolean;
}

const messageSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  receiver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  content: {
    type: String,
    required: true,
    trim: true
  },
  read: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
});

export const Message = mongoose.model<IMessage>('Message', messageSchema); 