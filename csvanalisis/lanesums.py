
# coding: utf-8

# In[12]:

import pandas as pd


# In[13]:

games = pd.read_csv("games.csv")
games['summoners'] = games.summoner1.astype(str).str.cat(games.summoner2.astype(str), sep='-')
wins = games.drop(games[games.win == False].index)
loses = games.drop(games[games.win == True].index)


# In[14]:

lanesumgroup = wins.groupby(['lane','summoners'])['win']
#lanesumgroup = wins.groupby(['lane','summoners','champ'])['win']


# In[15]:

lanesumframe = lanesumgroup.count().to_frame()


# In[16]:

ulsframe = lanesumframe.unstack(0)


# In[18]:

ulsframe.columns=['botwin', 'junwin', 'midwin', 'topwin']

bot = ulsframe['botwin']
mid = ulsframe['midwin']
jun = ulsframe['junwin']
top = ulsframe['topwin']

bot = bot.to_frame()
mid = mid.to_frame()
jun = jun.to_frame()
top = top.to_frame()


# In[19]:

bot[pd.notnull(bot['botwin'])].astype(int).to_csv('bot.csv')
mid[pd.notnull(mid['midwin'])].astype(int).to_csv('mid.csv')
jun[pd.notnull(jun['junwin'])].astype(int).to_csv('jun.csv')
top[pd.notnull(top['topwin'])].astype(int).to_csv('top.csv')


# In[ ]:



